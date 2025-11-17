/**
 * Created by huangjie on 2020-06-08
 */

// 补丁类
window.patchlist = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("ul");
    this._root.setAttribute("class", "patchslist clearfix");
    this._container.appendChild(this._root);
}
patchlist.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
patchlist.prototype.layout = function() {
    this._root.innerHTML = "";
    this._items = [];

    var data = this._data.patchs;
    if (data) {
        for (var i = 0; i < data.length; i++) {
            var item = new patch(this._root); // , {clickproc: clickproc}
            item.data(data[i]);
            this._items.push(item);
        }
    }
    var hisdata = this._data.historypatchs;
    if (hisdata) {
        for (var i = 0; i < hisdata.length; i++) {
            var item = new patch(this._root);
            item.data(hisdata[i]);
            item.status('upgraded');
            this._items.push(item);
        }
    }
}

// 单个补丁
window.patch = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("li");
    this._container.appendChild(this._root);
}
patch.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
patch.prototype.status = function(value) {
    if (value !== undefined) {
        this._status = value;
        if (value == 'upgraded') {
            addclass(this._root, 'upgraded');
        } else {
            removeclass(this._root, 'upgraded');
        }
    } else {
        return this._status || 'normal';
    }
}
patch.prototype.layout = function() {
    this._root.innerHTML = "";

    var data = this._data;
    this._root.innerHTML = data;
}
