/**
 * Created by huangjie on 2020-06-19
 */
window.flipper =  function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'page');
    this._container.appendChild(this._root);
}

flipper.prototype.layoutstatus = function(currentpage, totalpage) {
    var pageclickproc  = function(e) {
        var page = parseInt(e.currentTarget.getAttribute('pageindex'));
        if (page == -1) return;
        this.layoutstatus(page, totalpage);
        if (this._options && this._options.pageclickproc) {
            this._options.pageclickproc({page:page, total:totalpage});
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var status = null;
    if (totalpage <= 9) {
        status = 1;
    } else if (currentpage < 5) {
        status = 2;
    } else if (currentpage > totalpage - 6) {
        status = 3;
    } else {
        status = 4;
    }
    var content = '';
    switch (status) {
        case 1:
            for (var i = 0; i < totalpage; i++) {
                content += '<span pageindex="' + i + '" class="block' + (i == currentpage ? ' current' : '') + '">' + (i + 1) + '</span>';
            }
            break;
        case 2:
            for (var i = 0; i < 9; i++) {
                if (i == 7) {
                    content += '<span pageindex="-1" class="nullblock">&nbsp;</span>';
                } else if (i == 8) {
                    content += '<span pageindex="' + (totalpage - 1) + '" class="block">' + totalpage + '</span>'
                } else {
                    content += '<span pageindex="' + i + '" class="block' + (i == currentpage ? ' current' : '') + '">' + (i + 1) + '</span>';
                }
            }
            break;
        case 3:
            for (var i = 0; i < 9; i++) {
                if (i == 1) {
                    content += '<span pageindex="-1" class="nullblock">&nbsp;</span>';
                } else if (i == 0) {
                    content += '<span pageindex="0" class="block">1</span>'
                } else {
                    content += '<span pageindex="' + (totalpage + i - 9) + '" class="block' + (i == (9 + currentpage - totalpage) ? ' current' : '') + '">' + (totalpage + i - 8) + '</span>'
                }
            }
            break;
        case 4:
            for (var i = 0; i < 9; i++) {
                if (i == 1 || i == 7) {
                    content += '<span pageindex="-1" class="nullblock">&nbsp;</span>';
                } else if (i == 0) {
                    content += '<span pageindex="0" class="block">1</span>'
                } else if (i == 8) {
                    content += '<span pageindex="' + (totalpage - 1) + '" class="block">' + totalpage + '</span>'
                } else {
                    content += '<span pageindex="' + (currentpage + i - 4) + '" class="block' + (i == 4 ? ' current' : '') + '">' + (currentpage + i - 3) + '</span>'
                }
            }
            break;
    }
    this._root.innerHTML = content;
    for (var i = 0; i < this._root.children.length; i++) {
        var dom = this._root.children[i];
        if (dom) {
            dom.addEventListener('click', pageclickproc);
        }
    }
}
