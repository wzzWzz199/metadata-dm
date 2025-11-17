/**
 * Created by huangjie on 2020-06-16
 */

// 导出模块的按钮组
window.btngroup = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'btncon clearfix');
    this._container.appendChild(this._root);
}
btngroup.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
btngroup.prototype.selected = function(value) {
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
btngroup.prototype.layout = function() {
    var clickproc = function(e) {
        if (this._options && this._options.clickproc) {
            var type = e.currentTarget.getAttribute('name');
            for (var i = 0; i < this._data.length; i++) {
                if (this._data[i].type == type) {
                    this._options.clickproc(this._data[i]);
                    break;
                }
            }
        }
    }.bind(this);
    // if (this._root) {
    //     this._root.removeEventListener('click', clickproc)
    // }

    var data = this._data;

    this._root.innerHTML = "";
    var content = '';
    for (var i = 0; i < data.length; i++) {
        content += '<span name="' + data[i].type + '">' + data[i].name + '</span>';
    }
    this._root.innerHTML = content;

    for (var i = 0; i < this._root.childNodes.length; i++) {
        this._root.childNodes[i].addEventListener("click", clickproc);
    }
}
