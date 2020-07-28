import { FkEditor } from "./FkBoard.js";
import { concatTypedArrays} from "./helpers/Helper.js"

export class DataBridge {
    /**
     * Init data bridge.
     * 
     * @param {FkEditor} fkeditor
     */
    constructor(fkeditor) {
        this.receivers = {
            999: (json) => {
                this.serverVersion = json.serverVersion;
                this.askRulesList();
            }
        };
        this.authSent = false;
        this.fkeditor = fkeditor;
    }

    /**
     * 
     * @param {WebSocket} ws 
     */
    setWebSocket(ws) {
        this.ws = ws;
        this.ws.onerror = () => alert('Impossible de se connecter au serveur !');
        this.ws.onmessage = (event) => {
            if (event.data instanceof Blob) {
                event.data.arrayBuffer().then(blobBuffer => {
                    let iv = blobBuffer.slice(0, 12);
                    crypto.subtle.decrypt(
                        {
                            name: "AES-GCM",
                            iv: iv
                        },
                        this.fkeditor.secretKey,
                        blobBuffer.slice(12))
                        .then(buffer => {
                            this.onreceive(JSON.parse(new TextDecoder().decode(buffer)));
                        });
                });
            }
            else {
                this.onreceive(JSON.parse(event.data));
            }
        }
    }

    /**
     * Encrypt then  send the text
     * @param {String} text 
     */
    sendEncrypted(text) {
        if(this.mode === "PROXY") {
            let iv = crypto.getRandomValues(new Uint8Array(12));
            crypto.subtle.encrypt(
                {
                    name: "AES-GCM",
                    iv: iv
                },
                this.fkeditor.secretKey,
                new TextEncoder().encode(text))
                .then(buffer => this.ws.send(concatTypedArrays(iv, new Uint8Array(buffer))));
        }
        else {
            this.ws.send(text);
        }
    }

    /**
     * Use direct connection with password auth
     * 
     * @param {string} password
     */
    useLogin(password) {
        this.mode = "DIRECT";
        this.ws.onopen = () => this.login(password);
    }

    /**
     * Pass through the proxy and use id auth
     * 
     * @param {string} id
     */
    useProxy(id) {
        this.mode = "PROXY";
        this.ws.onopen = () => this.ws.send(JSON.stringify({ "code": 950, "id": `${id}`, "senderType": "fkboard" }));
    }


    /**
     * Add receiver for the given code.
     * 
     * @param {number} code 
     * @param {function(json)} callback
     */
    addReceiver(code, callback) {
        this.receivers[code] = callback;
    }

    /**
     * Send password to the server.
     * 
     * @param {string} password 
     */
    login(password) {
        this.ws.send(JSON.stringify({ 'action': `LOGIN ${password}` }));
        this.authSent = true;
    }

    /**
     * Inform the server that client wants to make a team change.
     * 
     * @param {string} playerName 
     * @param {string} teamName 
     */
    sendTeamMovement(playerName, teamName) {
        if (typeof playerName === 'undefined') {
            return;
        }
        this.sendEncrypted(JSON.stringify({ code: 2001, 'action': `MOVE`, 'player': playerName, 'team': teamName }));
    }

    /**
     * Send a team insertion request.
     * 
     * @param {string} teamName 
     */
    sendTeamInsertion(teamName) {
        if (typeof teamName === 'undefined') {
            return;
        }
        this.sendEncrypted(JSON.stringify({ code: 2002, 'action': `INSERT TEAM`, 'team': teamName }));
    }

    /**
     * Send a request for team name change.
     * 
     * @param {string} teamName 
     */
    sendTeamNameChange(previousName, newName) {
        this.sendEncrypted(JSON.stringify({ code: 2003, 'action': `CHANGE TEAM NAME`, 'previous': previousName, 'newName': newName }));
    }

    /**
     * Send a team deletion request.
     * 
     * @param {string} teamName 
     */
    sendTeamSuppression(teamName) {
        if (typeof teamName === 'undefined') {
            return;
        }
        this.sendEncrypted(JSON.stringify({ code: 2005, 'action': `DELETE TEAM`, 'team': teamName }));
    }

    askRulesList() {
        this.sendEncrypted(JSON.stringify({ code: 2006, 'action': `LIST RULES` }));
    }

    /**
     * Send a rule change.
     */
    sendRuleChange(rule, value) {
        this.sendEncrypted(JSON.stringify({ code: 2007, 'action': `EDIT RULE`, 'rule': rule, 'value': value }));
        this.fetchScoreboardContent();
    }

    fetchScoreboardContent() {
        this.sendEncrypted(JSON.stringify({ code: 2008, 'action': `FETCH SCOREBOARD` }));
    }

    updateScoreboard(lines) {
        this.sendEncrypted(JSON.stringify({ code: 2009, 'action': `UPDATE SCOREBOARD`, lines }));
    }

    /**
     * @private
     */
    onreceive(json) {
        if (json.code in this.receivers) {
            this.receivers[json.code](json);
        } else {
            console.log('Received', json);
        }
    }
}