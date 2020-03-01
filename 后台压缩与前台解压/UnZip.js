/**
 * Created by pxc on 2020-01-15.
 */
define([
    "../common/pako.js"
], function (pako) {
    return {

        UnZip:function (tmp){
        let strData = atob(tmp);

        // Convert binary string to character-number array
        let charData = strData.split('').map(function (x) { return x.charCodeAt(0); });
        // Turn number array into byte-array
        let binData = new Uint8Array(charData);
        // // unzip
        return JSON.parse(pako.inflate(binData, { to: 'string' }));

    }}
});