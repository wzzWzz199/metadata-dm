/**
 * Created by huangjie on 2020-06-03
 */

// 环境
window.clusterlist = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('ul');
    this._root.setAttribute('class', 'clusterlist clearfix');
    this._container.appendChild(this._root);
}
clusterlist.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
clusterlist.prototype.layout = function() {
    var clickproc = function(data) {
        this.current(data);
    }.bind(this);

    var data = this._data;

    this._root.innerHTML = '';
    this._items = [];

    for (var i = 0; i < data.length; i++) {
        var item = new cluster(this._root, {clickproc:clickproc});
        item.data(data[i]);
        this._items.push(item);
    }
    if (data.length) {
        this.current(data[0]);
    }
}
clusterlist.prototype.getitem = function(data) {
    if (!this._data) return null;
    var index = this._data.indexOf(data);
    if (index >= 0) return this._items[index];
    return null;
}
clusterlist.prototype.current = function(value) {
    if (value !== undefined) {
        if (this._current == value) return;
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
// clusterlist.prototype.currentitem = function() {
//     return this.getitem(this._current);
// }

window.cluster = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('li');
    this._root.setAttribute('class', 'cluster');
    this._container.appendChild(this._root);
}
cluster.STATUS_MAP = {
    0:'notupgraded',   // 可升级
    1:'upgradding', // 升级中
    2:'upgraderror',  // 升级异常
    3:'upgraded'  // 升级完成
};
cluster.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
cluster.prototype.status = function(value) {
    if (value !== undefined) {
        if (this._status == value) return;
        if (this._status != null) {
            window.removeclass(this._root, cluster.STATUS_MAP[this._status]);
        }
        this._status = value;
        if (value != null) {
            window.addclass(this._root, cluster.STATUS_MAP[value]);
        }
    } else {
        return this._status;
    }
}
cluster.prototype.selected = function(value) {
    if (value !== undefined) {
        this._selected = value;
        if (value) {
            window.addclass(this._root, 'selected');
        } else {
            window.removeclass(this._root, 'selected');
        }
    } else {
        return this._selected || false;
    }
}
cluster.prototype.layout = function() {
    var clickproc = function(e) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(this._data);
        }
    }.bind(this);

    if (this._env) {
        this._env.removeEventListener('click', clickproc)
    }
    this._root.innerHTML = '';

    var data = this._data;
    var content = [
        '<div>',
            '<div class="detail">',
                '<span class="name">' + data.name + '</span>',
                '<div class="proinfowrap clearfix">',
                    '<span title="' + data.appserver + '">应用服务器:' + data.appserver + '</span>',
                    '<span title="' + data.dataserver + '">数据库:' + data.dataserver + '</span>',
                '</div>',
                '<span class="status"></span>',
            '</div>',
        '</div>',
    ].join('');
    this._root.innerHTML = content;
    this._env = this._root.childNodes[0].childNodes[0];
    this._env.addEventListener('click', clickproc);
}
