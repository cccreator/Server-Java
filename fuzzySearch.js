var _timer; //定义一个时间变量
        $("#compreCarNoFill").on("input", function () {
            $("#inputSelectContentCompre").empty();
            var inputStr = $("#compreCarNoFill").val();
            if (inputStr.length >= 0) {//从第一位就模糊
                if (_timer) {//若第二次键入时间在0.5s内，则不触发搜索
                    clearTimeout(_timer);
                }
                _timer = setTimeout(function () {
                    var htmlStr = "";
                    $.ajax({
                        type: "POST",
                        url: getWebRootPath() + '/BusMonitor/GISMonitor/getCarNoOrRoute',
                        data: {
                            "inputStr": inputStr
                        },
                        dataType: "json",
                        success: function (obj) { 
                            htmlStr += "<ul  style='list-style-type:none;margin-bottom: 0px;'>";
                            $.each(obj, function (index, value) {
                                var picUrlName = value.inputType == 1 ? '../resources/skin/szSkin/image/busLineTooltipPic.png' : '../resources/skin/szSkin/image/compreBusPic.png';
                                htmlStr += "<li onclick='clickFunction(this)' relType='" + value.inputType + "'  relId='" + value.inputId + "' relName='" + value.inputName + "'>" +
                                    "                    <span class=\"busItemTooltipPic\" style='background-image: url(" + picUrlName + ")'></span>" +
                                    "                    <span  class=\"busItemTooltipName\">" + value.inputName + "</span>" +
                                    "                    <span class=\"busItemTooltipCompany\">" + value.parentOrgName + "</span>" +
                                    "                </li>";
                            });
                            htmlStr += "</ul>";
                            $("#inputSelectContentCompre").append(htmlStr);
                            $("#inputSelectContentCompre").css('display', 'block');
                        }
                    });
                }, 500);//延迟0.5s
            }
            /**************点击空白处，下拉框消失***********************/
            event.stopPropagation();//阻止事件冒泡
            $(this).siblings('#inputSelectContentCompre').toggle();
            //点击空白处，下拉框隐藏-------开始
            var tag = $(this).siblings('#inputSelectContentCompre');
            var flag = true;
            $(document).bind("click", function (e) {//点击空白处，设置的弹框消失
                var target = $(e.target);
                if (target.closest(tag).length == 0 && flag == true) {
                    $(tag).hide();
                    flag = false;
                }
            });
            /**************点击空白处，下拉框消失***********************/
        });
        clickFunction = function (obj) {//click选中下拉框事件
            $("#compreCarNoFill").val($(obj).attr("relName"));
            $("#compreCarNoInputHideWarning").hide();
            if ($(obj).attr("relType") == '2') { //搜车牌号
                $.ajax({
                    type: "POST",
                    url: getWebRootPath() + '/BusMonitor/GISMonitor/isBusOnLine',
                    data: {
                        "carNo": $(obj).attr("relName")
                    },
                    dataType: "json",
                    success: function (data) {
                        if (data.length == 0) {
                            $("#compreCarNoInputHideWarning").show();
                        } else {
                            var carNo = $(obj).attr("relName");
                            if (carNo != "") {
                                $("#compreCarNoInputHideWarning").hide();
                                $("#compreCarNoFill").val("");
                                $("#justForRemove").remove();
                                $("#inputSelectContentCompre").empty();
                                document.getElementById("comprehenShow").contentWindow.app.inputCarNo(carNo);
                            }
                        }
                    }
                })
            } else if ($(obj).attr("relType") == '1') {//搜线路
                $.ajax({
                    type: "POST",
                    url: getWebRootPath() + '/BusMonitor/GISMonitor/getCarNoByRouteId',
                    data: {"routeId": $(obj).attr("relId")},
                    dataType: "json",
                    success: function (data) {
                        var carNo = data[0];
                        if (carNo != "") {
                            $("#compreCarNoInputHideWarning").hide();
                            $("#compreCarNoFill").val("");
                            $("#justForRemove").remove();
                            $("#inputSelectContentCompre").empty();
                            document.getElementById("comprehenShow").contentWindow.app.inputCarNo(carNo);
                        }
                    }
                });
            }

            // $("#inputSelectContentCompre").hide();
            $("#inputSelectContentCompre").empty();
        };