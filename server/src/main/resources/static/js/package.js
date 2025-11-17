/**
 * Created by huangjie on 2020-06-08
 */

// 包
window.packagelist = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("ul");
    this._root.setAttribute("class", "packageul clearfix");
    this._container.appendChild(this._root);
}
packagelist.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
packagelist.prototype.layout = function() {
    var clickproc = function(data) {
        // console.log('data', data);
        this.current(data);
    }.bind(this);
    var data = this._data;

    this._root.innerHTML = "";
    this._items = [];
    for (var i = 0; i < data.length; i++) {
        var item = new package(this._root, {clickproc: clickproc});
        item.data(data[i]);
        this._items.push(item);
    }
    if (data.length) {
        this.current(data[0]);
    }
}
packagelist.prototype.getitem = function(data) { // 通过data找item
    if (!this._data) return null;
    var index = this._data.indexOf(data);
    if (index >= 0) return this._items[index];
    return null;
}
packagelist.prototype.current = function(value) {
    if (value !== undefined) {
        // if (this._current == value) return;
        if (this._current) {
            var item = this.getitem(this._current);
            if (item) item.selected(false);
        }
        this._current = value;
        if (value) {
            var item = this.getitem(value);
            if (item) item.selected(true);
        }
        if (this._options && this._options.clickproc) {
            this._options.clickproc(value);
        }
    } else {
        return this._current;
    }
}

// 单个包
window.package = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("li");
    this._root.setAttribute("class", "package");
    this._container.appendChild(this._root);
}
package.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
package.prototype.selected = function(value) {
    if (value !== undefined) {
        this._selected = value;
        if (value) {
            window.addclass(this._root, "selected");
        } else {
            window.removeclass(this._root, "selected");
        }
    } else {
        return this._selected || false;
    }
}
package.prototype.layout = function() {
    var clickproc = function(e) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);

    if (this._root) {
        this._root.addEventListener("click", clickproc);
    }
    this._root.innerHTML = "";

    var data = this._data;
    this._root.innerHTML = data.ver;
}
