/**
 * Created by huangjie on 2020-06-03
 */

// 日志类
window.log = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("div");
    // this._root.setAttribute("class", "log");
    this._container.appendChild(this._root);
}
log.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this._root.innerHTML = "";
        this.addlogs(value);
    } else {
        return this._data;
    }
}
log.prototype.clearlogs = function() {
    this._data = null;
    this._root.innerHTML = "";
}
log.prototype.addlogs = function(logs) {
    var logcontent = '';
    for (var i = 0; i < logs.length; i++) {
        var log = logs[i];
        logcontent += [
                '<span>' + new Date(log.datetime).format('MM-dd hh:mm:ss') + '</span>',
                '<span>' + log.msg + '</span><br>'
        ].join('');
    }
    this._root.innerHTML += logcontent;
    this._root.scrollTop = this._root.scrollHeight - this._root.offsetHeight;

}
