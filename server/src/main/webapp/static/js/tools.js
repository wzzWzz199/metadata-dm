/**
 * Created by huangjie on 2020-06-03
 */
window.addclass = function(dom, cn) {
    var cls = dom.getAttribute('class') || '';
    if (new RegExp("(^|\\s)" + cn + "(\\s|$)").test(cls)) return;
    dom.setAttribute('class', (cls + ' ' + cn).replace(/(^\s+|\s+$)/, ''));
}
window.removeclass = function(dom, cn) {
    dom.setAttribute('class', (dom.getAttribute('class') || '').replace(new RegExp("(^|\\s)" + cn + "(\\s|$)"), ' ').replace(/(^\s|\s$)/g, ''))
}
window.$type = {
    OBJECT: "OBJECT",
    ARRAY: "ARRAY",
    STRING: "STRING",
    BOOLEAN: "BOOLEAN",
    NUMBER: "NUMBER",
    DATE: "DATE",
    NULL: "NULL",
    UNDEFINED: "UNDEFINED",
    FUNCTION: "FUNCTION",

    is: function(data, type) {
        var t = "";
        if (data === null) t = "null";
        else if (data === undefined) t = "undefined";
        else t = Object.prototype.toString.call(data).replace(/\[.+\s([^\]]+)\]/, "$1").toUpperCase();
        if (/HTML.*ELEMENT/i.test(t)) t = this.OBJECT;
        if (type) return t == type.toUpperCase();
        else return t;
    },
    ins: function(data, type) {
        return type.indexOf(this.is(data)) >= 0;
    }
}
if (!String.prototype.addurlparam) String.prototype.addurlparam = function(param, excepts) {
    if (!param) return this;

    function getparam(param, excepts) {
        var type = $type.is(param);
        switch (type) {
            case $type.OBJECT:
                var s = "";
                for (var i in param) {
                    if (excepts && excepts.indexOf(i) >= 0) continue;
                    if (param[i] != undefined) s += "&" + i + "=" + encodeURIComponent(param[i]);
                }
                return s;
            case $type.ARRAY:
                var s = "";
                for (var i = 0; i < param.length; i++) {
                    s += "&" + param[i];
                }
                return s;
            default:
                return "&" + param;
        }
    }

    var ext = "";
    var path = this;
    var index = this.indexOf("#");
    if (index >= 0) {
        path = this.substr(0, index);
        ext = this.substr(index);
    }
    var s = getparam(param, excepts);
    if (this.indexOf("?") < 0) {
        s = s.replace(/\&/, "?");
    }
    return path + s + ext;
}
if (!String.prototype.geturlparam) String.prototype.geturlparam = function(param) {
    var reg = new RegExp("[?&]" + param + "=([^?&#]*)");
    return this.match(reg) ? decodeURIComponent(RegExp.$1) : null;
}
window.request = function(url) {
    function handleresult(req, success, failed) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var data = req.responseText;
                try {
                    data = JSON.parse(data);
                } catch (e) {}
                if (success) success(data);
                return data;
            } else {
                var data = {status:req.status, content:req.responseText};
                if (failed) failed(data);
                return null;
            }
        }
    }

    var req = new XMLHttpRequest();
    var type = "GET";
    var async = true;
    var data = null;
    var head = null;
    var contenttype = "application/json;charset=utf-8";
    var success = undefined;
    var failed = undefined;
    for (var i = 1; i < arguments.length; i++) {
        var arg = arguments[i];
        if (arg === true || arg === false) {
            async = arg; continue;
        } else if (/^(GET|POST|PUT|DELETE)$/i.test(arg)) {
            type = arg.toUpperCase(); continue;
        } else if (/\w+\/[\w\-\;]+/.test(arg)) {
            contenttype = arg; continue;
        } else {
            var t = $type.is(arg);
            if (t == $type.NULL || t == $type.FUNCTION) {
                if (success === undefined) success = arg;
                else failed = arg;
                continue;
            } else {
                if (arg.param) url = url.addurlparam(arg.param);
                if (arg.data) data = arg.data;
                if (arg.head) head = arg.head;
            }
        }
    }
    req.open(type, url, async);
    req.withCredentials = true;
    req.setRequestHeader("Accept", "application/json;charset=utf-8");
    req.setRequestHeader("Content-Type", contenttype);
    if (head) for (var i in head) req.setRequestHeader(i, head[i]);

    var result = req;
    req.onreadystatechange = function() {
        result = handleresult(this.req, this.success, this.failed);
    }.bind({req:req, url:url, type:type, async:async, data:data, contenttype:contenttype, success:success, failed:failed});
    req.send(data ? JSON.stringify(data) : undefined);
    return result;
}

if (Date.prototype.format === undefined) {
    Date.prototype.format = function(y) {
        if (y == undefined) y = "yyyy-MM-dd";
        var z = {y:this.getFullYear(), M:this.getMonth()+1, d:this.getDate(), h:this.getHours(), m:this.getMinutes(), s:this.getSeconds()};
        return y.replace(/(y+|M+|d+|h+|m+|s+)/g,function(v) {return ((v.length>1?"0":"")+eval('z.'+v.slice(-1))).slice(-(v.length>2?v.length:2))});
    }
}
window.download = function(url, type, data) {
    url.addurlparam(Math.random());

    if (type && type.toUpperCase() == "POST") {
        function getframe() {
            if (!window.DOWNLOAD_FRAME) {
                window.DOWNLOAD_FRAME = document.createElement('iframe');
                window.DOWNLOAD_FRAME.setAttribute('src', 'about:blank');
                window.DOWNLOAD_FRAME.setAttribute('name', 'download');
                window.DOWNLOAD_FRAME.style.display = 'none';
                document.body.appendChild(window.DOWNLOAD_FRAME);
            }
            return window.DOWNLOAD_FRAME;
        }
        function postdownload(params, url) {
            var form = document.createElement("form");
            form.style.display = "none";
            form.action = url;
            form.method = "POST";
            form.setAttribute('target', 'download');
            document.body.appendChild(form);
            for (var key in params) {
                var input = document.createElement("input");
                input.type = "hidden";
                input.name = key;
                input.value = params[key];
                form.appendChild(input);
            }

            form.submit();
            form.remove();
        }
        postdownload(data, url);
    } else {
        window.open(url, "_blank");
    }
}
