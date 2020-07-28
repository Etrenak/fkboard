var cache = [];

export async function getBlocksList(dataBridge) {
    if (cache.length !== 0) {
        return new Promise((resolve) => resolve(cache));
    }
    let version = dataBridge.serverVersion;
    let tries = 0;
    while (cache.length === 0) {
        cache = await fetch(`https://unpkg.com/minecraft-data@2.51.0/minecraft-data/data/pc/${version}/blocks.json`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                return response.json();
            }
            else {
                if(version.match(/^\d+\.[78](\.\d+)?$/) !== null) {
                    version = version.replace(/^(\d+\.[78])\.?\d*$/, "$1");
                }
                else if(tries < 5) {
                    version = version.replace(/^(\d+\.\d+\.?)(\d*)$/, (match, majorMinorGroup, patchGroup) => majorMinorGroup + (parseInt(patchGroup) + 1).toString());
                    tries++;
                }
                else {
                    version = "1.15.2"
                }
                return [];
            }
        });
    }
    return cache;
}