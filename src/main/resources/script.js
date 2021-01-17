const API = 'https://inventario.vialineperu.com/qr/encode'

var txtInput;
var sizeInput;

async function encode() {
    if (!verify()) {
        alert('Empty')
        return
    }

    const txt = txtInput.value
    const size = sizeInput.value
    const url = API + '?txt=' + txt + '&size=' + size

    fetch(url, {
        method: 'POST',
    }).then(res => res.blob())
    .then(img => {
        const name_split = txt.split("/")
        const name = name_split.length < 3? txt : name_split[2]
        download(img, name)
    })
}

function verify() {
    txtInput = document.getElementById('txt')
    sizeInput = document.getElementById('size')
    return (
        (txtInput.value != undefined && txtInput.value != '') 
        && (sizeInput.value != undefined && sizeInput.value != '') 
    )
}