/**
 * Created by huangjie on 2020-06-03
 */

// 模块
window.modulelist = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("ul");
    this._root.setAttribute("class", "modulelist clearfix");
    this._container.appendChild(this._root);
}
modulelist.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
modulelist.prototype.layout = function() {
    var clickproc = function(data) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(data);
        }
    }.bind(this);
    var data = this._data;

    this._root.innerHTML = "";
    this._items = [];

    for (var i = 0; i < data.length; i++) {
        var item = new module(this._root, {clickproc: clickproc});
        item.data(data[i]);
        this._items.push(item);
    }
    // if (data.length) {
    //     this.current(data[0]);
    // }
}
modulelist.prototype.getitembycode = function(code) {
    for (var i = 0; i < this._data.length; i++) {
        var d = this._data[i];
        if (d.code == code) {
            return this._items[i];
        }
    }
    return null;
}
// 添加一条数据
modulelist.prototype.addonedata = function() {
    var clickproc = function(data) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(data);
        }
    }.bind(this);
    var data = this._data;
    var item = new module(this._root, {clickproc:clickproc});
    item.data(data);
    for (var i = 0; i < data.length; i++) {
        item.status(data[i].status);
    }
    this._items.push(item);
}
modulelist.prototype.addlogs = function(logs) {
    for (var i = 0; i < logs.length; i++) {
        var logobj = logs[i];
        var module = this.getitembycode(logobj.code);
        if (!module) continue;
        if (logobj.logs.length) module.addlogs(logobj.logs);
    }
}
modulelist.prototype.getlogtime = function() {
    var logtime = 0;
    for (var i = 0; i < this._items.length; i++) {
        var moduledata = this._items[i].data();
        var logs = moduledata.logs;
        if (logs && logs.length > 0) {
            logtime = Math.max(logtime, logs[logs.length - 1].datetime);
        }
    }
    return logtime;
}

// 单个模块
window.module = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement("li");
    this._root.setAttribute("class", "module");
    this._container.appendChild(this._root);
}
module.STATUS_MAP = {
    0:'notupgraded',   // 可升级
    1:'upgradding', // 升级中
    2:'upgraderror',  // 升级异常
    3:'upgraded'  // 升级完成
}
module.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
module.prototype.status = function(value) {
    if (value !== undefined) {
        this.showlog();
        this.showdatetime();
        if (this._status == value) return;

        this._data.status = value;
        // 状态改变后清空日志
        this._data.logs = [];
        if (this._status != null) {
            window.removeclass(this._root, module.STATUS_MAP[this._status]);
        }
        this._status = value;
        if (value != null) {
            window.addclass(this._root, module.STATUS_MAP[value]);
        }
    } else {
        return this._status;
    }
}
module.prototype.selected = function(value) {
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
module.prototype.layout = function() {
    var clickproc = function(e) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var btnproc = function(e) {
        if (this._options && this._options.btnproc) {
            this._options.btnproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);
    if (this._root) {
        this._root.addEventListener("click", clickproc);
    }
    this._root.innerHTML = "";

    var data = this._data;
    var content = [
        '<div>',
            '<div>',
                '<div class="content">',
                    '<div class="detail">',
                        '<span class="code">' + data.code + '</span>',
                        '<span class="name">' + data.name + '</span>',
                    '</div>',
                    '<div class="btn">升级</div>',
                    '<span class="status"></span>',
                '</div>',
                '<div class="log clearfix">' +
                    '<span class="version">' + (data.currentver || '') + '</span>' +
                    '<span class="info"></span>' +
                '</div>',
                '<div class="progress"><div></div></div>',
            '</div>',
        '</div>'
    ].join("");
    this._root.innerHTML = content;
    this._root.childNodes[0].childNodes[0].addEventListener("click", clickproc);
    // this._root.childNodes[0].childNodes[0].childNodes[0].childNodes[0].addEventListener("click", clickproc);
    // this._root.childNodes[0].childNodes[0].childNodes[0].childNodes[1].addEventListener("click", btnproc);
    this._infocontainer = this._root.childNodes[0].childNodes[0].childNodes[1].childNodes[1];
    this._progressbar = this._root.childNodes[0].childNodes[0].childNodes[2].childNodes[0];
    this.status(data.status);
    // this.datetime();
}
module.prototype.progress = function(value) {
    if (value !== undefined) {
        this._progress = value;
        if (value > 100) value = 100;
        if (value < 0) value = 0;
        if (value == 100) {
            this.status(3);
        } else {
            this._progressbar.style.width = (value || 0) + '%';
        }
    } else {
        return this._progress;
    }
}

module.prototype.addlogs = function(logs) {
    if (!this._data || !logs.length) return;
    if (!this._data.logs) this._data.logs = logs;
    else this._data.logs = this._data.logs.concat(logs);
    if (this._data.logs.length) {
        var log = this._data.logs[this._data.logs.length - 1];
        this.status(log.status);
        this.progress(parseFloat(log.progress));
    }
}

module.prototype.showlog = function() {
    if (this._status != 1 && this._status != 2) return;
    if (!this._data.logs.length) return;
    var logobj = this._data.logs[this._data.logs.length - 1];
    var log = logobj.msg;
    if (log) {
        this._infocontainer.innerHTML = log;
    }
}
module.prototype.showdatetime = function() {
    if (this._status == 1 || this._status == 2) return;
    this._infocontainer.innerHTML = new Date(this._data.datetime).format('yyyy-MM-dd hh:mm');
}
