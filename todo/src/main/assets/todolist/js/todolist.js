$(function () {
    $("#title").on("keydown", function (event) {
        if (event.keyCode === 13) {
            if($(this).val() === "") {
                alert("请输入您的代办事项")
                return
            }
            //保存输入事项到本地
            var local = getData();
            local.push({
                title: $(this).val(),
                done: false
            });
            //清空输入框的内容
            $(this).val("");
            saveData(local);
            //重新渲染页面
            load();
        }
    });
    //勾选按钮
    $("ol, ul").on("click", "input", function() {
        var index = $(this).siblings("a").attr("id");
        var isChecked = $(this).prop("checked");
        console.log("勾选切换：index=" + index + "checked=" + isChecked);
        //获取数据
        var data = getData();
        //修改数据
        data[index].done = isChecked;
        //保存，并重新渲染
        saveData(data);
        load();
    });
    //删除按钮
    $("ol, ul").on("click", "a", function () {
        var data = getData();
        //删除，并重新保存数据
        var index = $(this).attr("id");
        console.log("删除，id：" + index);
        data.splice(index, 1);
        saveData(data);
        //重新渲染页面
        load();
    });
    //第一次渲染页面
    load();
});

/**
 * 加载数据，渲染页面
 */
function load() {
    //渲染前，将列表清空
    $("ul, ol").empty();
    var data = getData();
    //正在进行的个数
    var todoCount = 0;
    //已经完成的个数
    var doneCount = 0;
    $.each(data, function (i, n) {
        //创建item，并给每个item都设置一个id
        var li = $("<li><input type='checkbox'> <p>" + n.title + "</p> <a href='javascript:;'></a></li>")
        li.find("a").attr("id", i);
        if(n.done) {
            doneCount++;
            //已完成
            $("ul").prepend(li);
        } else {
            todoCount++;
            //未完成
            $("ol").prepend(li);
        }
        //回显勾选状态
        $(li).find("input").prop("checked", n.done);
    });
    //统计数量
    $("#todocount").text(todoCount);
    $("#donecount").text(doneCount);
}

/**
 * 保存数据到本地
 */
function saveData(data) {
    localStorage.setItem("todolist", JSON.stringify(data));
}

/**
 * 读取本地保存的数据
 */
function getData() {
    var data = localStorage.getItem("todolist");
    if (data !== null) {
        return JSON.parse(data);
    } else {
        return [];
    }
}