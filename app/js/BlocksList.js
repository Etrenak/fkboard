var cache = [];

export async function getBlocksList(dataBridge) {
    if (cache.length !== 0) {
        return new Promise((resolve) => resolve(cache));
    }
    let version = dataBridge.serverVersion;
    while (cache.length == 0) {
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
                version = version.replace(/(\d+\.\d+\.?)(\d*)/, (match, majorMinorGroup, patchGroup) => majorMinorGroup + (parseInt(patchGroup) + 1).toString());
                return [];
            }
        });
    }
    return cache;
}