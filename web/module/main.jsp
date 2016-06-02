<%--
  Created by IntelliJ IDEA.
  User: Bourne
  Date: 16-5-18
  Time: 上午9:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>大亚湾核电文档知识管理系统</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/font-awesome.css" rel="stylesheet">
    <link href="../css/ace.min.css" rel="stylesheet">
    <link href="../css/base.css" rel="stylesheet">

    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/cgn-elements.min.js"></script>
    <script type="text/javascript" src="../js/cgn.min.js"></script>

    <!--Tab选项卡-->
    <script src="../js/jquery-1.12.4.min.js"></script>
    <script src="../js/bootstrap.js"></script>

    <!--轮播-->
    <script type="text/javascript" src="../js/mySystem.js"></script>
    <script>
        $(function(){
            //回调函数计数
            var callbackIndex = 0;
            $('.silder-box-1').mySilder({
                width:270, //容器的宽度 必选参数!!!!!!
                height:258, //容器的高度 必选参数!!!!!!
                auto:true,//是否自动滚动
                autoTime:5, //自动滚动时，时间间隙，即多长滚动一次,单位(秒)
                direction:'x',//滚动方向,默认X方向
                autoType:'left', //滚动方向，auto为true时生效
                few:1,//一次滚动几个，默认滚动1张
                showFew:1, //显示几个,就不用调css了,默认显示一个
                clearance:0, //容器之间的间隙，默认为 0
                silderMode:'linear' ,//滚动方式
                timeGap:650,//动画间隙，完成一次动画需要多长时间，默认700ms
                buttonPre:'.silder-button.btl',//上一个，按钮
                buttonNext:'.silder-button.btr',//下一个，按钮
                jz:true, //点击时，是否等待动画完成
                runEnd:function(){//回调函数
                    callbackIndex ++ ;
                    $('.cj em').text(callbackIndex);
                }
            });
        });
    </script>

</head>

<body>
<div class="navbar navbar-inverse">
    <div class="navbar-inner">
        <div class="container-fluid">
            <div class="navbar-brand"><img src="images/logo.png"></div>
            <ul class="cgn_nav pull-right">
                <li><a href="#">企业外网</a>｜</li>
                <li><a href="#">返回首页</a></li>
            </ul>
            <ul class="nav ace-nav pull-right">
                <li class="active"><a href="#">首页</a></li>
                <li><a href="#">搜索中心</a></li>
                <li><a href="#">文档中心</a></li>
                <li><a href="#">专题专栏</a></li>
                <li><a href="#">帮助中心</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container-fluid" id="main-container">
<div id="sidebar">
    <ul class="nav nav-list">
        <li>
            <a href="#" class="dropdown-toggle"><span>个人任务</span></a>
            <ul class="submenu">
                <li><a href="#">待办工作</a></li>
                <li><a href="#">已办工作</a></li>
                <li><a href="#">我的申请</a></li>
                <li><a href="#">草稿箱</a></li>
            </ul>
        </li>
        <li>
            <a href="#"><span>文档编写</span></a>
        </li>
        <li>
            <a href="#"><span>文档借阅</span></a>
        </li>
        <li>
            <a href="#" class="dropdown-toggle"><span>问题反馈</span></a>
            <ul class="submenu">
                <li>
                    <div class="nav_img left">
                        <img src="images/nav_icon1.png">
                        <a href="#">文件包搜索</a>
                    </div>
                    <div class="nav_img right">
                        <img src="images/nav_icon2.png">
                        <a href="#">部门文件</a>
                    </div>
                    <div class="clearfix"></div>
                </li>
            </ul>
        </li>
    </ul>
    <div id="sidebar-collapse"><i class="icon-double-angle-left"></i></div>
</div>

<div id="main-content" class="clearfix">
    <div class="column1">
        <div class="searchbox">
            <ul class="nav nav-tabs">
                <li class="active"><a href="#quanwen" data-toggle="tab"><span>全文搜索</span></a></li>
                <li><a href="#kapian" data-toggle="tab"><span>卡片搜索</span></a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane fade active in" id="quanwen">
                    <div class="input-group">
                        <input type="text" class="form-control">
							<span class="input-group-btn">
								<a href="#"class="btn btn-danger"><img src="images/search_btn.png"></a>
							</span>
                    </div>
                    <div class="searchcheck clearfix">
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>全部</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" checked value=""><span class="fa fa-check"></span>程序</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" checked value=""><span class="fa fa-check"></span>技术支持文件</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>函件</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>会议纪要</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>备忘录</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>报告</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>合同</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>档案</label>
                            </div>
                        </div>
                        <div class="checkbox_small">
                            <div class="checkbox c-checkbox">
                                <label><input type="checkbox" value=""><span class="fa fa-check"></span>部门</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-pane fade in" id="kapian">
                    222
                </div>
            </div>
        </div>

        <div class="dbbox">
            <dl class="dbat clearfix">
                <dt>待办工作</dt>
                <dd><a href="#">>></a></dd>
            </dl>
            <ul class="dbac">
                <li><i class="red">●</i> 大型仪器整修报表大型仪器整修报表<a href="#">审查</a><span>2016-05-31</span></li>
                <li><i class="green">●</i> 大型仪器整修报表<a href="#">批准</a><span>2016-05-31</span></li>
                <li><i class="red">●</i> 大型仪器整修报表大型仪器整修报表<a href="#">审查</a><span>2016-05-31</span></li>
                <li><i class="red">●</i> 大型仪器整修报表大型仪器整修报表<a href="#">审查</a><span>2016-05-31</span></li>
                <li><i class="yellow">●</i> 大型仪器整修报表大型仪器整修报表<a href="#">校对</a><span>2016-05-31</span></li>
                <li><i class="green">●</i> 大型仪器整修报表<a href="#">批准</a><span>2016-05-31</span></li>
            </ul>
        </div>
        <div class="wdbox clearfix">
            <div class="wdbox_col left">
                <dl class="dbat clearfix">
                    <dt>公司文档更新</dt>
                    <dd><a href="#">>></a></dd>
                </dl>
                <ul class="wdbox_col_ac">
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                </ul>
            </div>
            <div class="wdbox_col right">
                <dl class="dbat clearfix">
                    <dt>部门文档更新</dt>
                    <dd><a href="#">>></a></dd>
                </dl>
                <ul class="wdbox_col_ac">
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                    <li><span>2016-05-31</span><a href="#">维修处季度报告总结说明</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="column2">
        <div class="grzx">
            <dl class="dbat clearfix">
                <dt>个人中心</dt>
                <dd><a href="#">>></a></dd>
            </dl>
            <div class="grzxac clearfix">
                <div class="grzxacbox">
                    <img src="images/grzx_icon01.png">
                    <p>我的信息</p>
                </div>
                <div class="grzxacbox">
                    <img src="images/grzx_icon02.png">
                    <p>我的授权</p>
                </div>
                <div class="grzxacbox">
                    <img src="images/grzx_icon03.png">
                    <p>我的收藏</p>
                </div>
            </div>
        </div>
        <div class="news">
            <dl class="dbat clearfix">
                <dt>通知新闻</dt>
                <dd><a href="#">>></a></dd>
            </dl>
            <ul class="newsac">
                <li><span>2016-06-01</span><a href="#">维修处季度报告总结说明</a></li>
                <li><span>2016-06-01</span><a href="#">计划科外审送签函</a></li>
                <li><span>2016-06-01</span><a href="#">宣传部最新宣传流程报告</a></li>
                <li><span>2016-06-01</span><a href="#">计划科外审送签函（一）</a></li>
                <li><span>2016-06-01</span><a href="#">维修处季度报告总结说明</a></li>
                <li><span>2016-06-01</span><a href="#">维修处季度报告总结说明</a></li>
            </ul>
        </div>
        <div class="tjbb">
            <dl class="dbat clearfix">
                <dt>统计报表</dt>
                <dd><a href="#">>></a></dd>
            </dl>
            <div class="tjbbac">
                <div class='silder-box silder-box-1'>
                    <div class='silder'>
                        <ul>
                            <li>
                                111
                            </li>
                            <li>
                                222
                            </li>
                            <li>
                                333
                            </li>
                            <li>
                                444
                            </li>
                            <li>
                                555
                            </li>
                            <li>
                                666
                            </li>
                        </ul>
                    </div>
                    <div class="silder-button btl"><i class="fa fa-angle-left"></i></div>
                    <div class="silder-button btr"><i class="fa fa-angle-right"></i></div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

<div class="footer">
    <div class="bottomnav">
        <a href="#">联系我们</a>|
        <a href="#">常见问题解答</a>|
        <a href="#">网站标志</a>|
        <a href="#">网站地图</a>|
        <a>技术支持：信息技术中心</a>
    </div>
    <p>Copyright &copy; 2015 中国广核集团有限公司</p>
</div>

</body>
</html>
