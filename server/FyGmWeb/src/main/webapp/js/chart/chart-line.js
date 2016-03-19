/**
 * 
 * @param renderTo 图表放置的div的ID
 * @param title	图表的标题
 * @param subTitle 图表的副标题
 * @param categories 图表的x轴显示的数组 ['a','b','c']
 * @param yAxis y轴的名称
 * @param xAxis x轴的名称
 * @param tooltip 鼠标经过时的提示单位
 * @param series 具体数据格式 如下
 * 			[{
                name: 'Tokyo',
                data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
            }, {
                name: 'New York',
                data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
            }, {
                name: 'Berlin',
                data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
            }, {
                name: 'London',
                data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
            }]
 */
function initLineChart (renderTo,title,subTitle,categories,yAxis,xAxis,tooltip,series) {
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: renderTo,
                type: 'line',
                marginRight: 130,
                marginBottom: 35
            },
            title: {
                text: title,
                x: -20 //center
            },
            subtitle: {
                text: subTitle,
                x: -20
            },
            xAxis: {
            	title: {
                    text: xAxis
                },
                categories: categories
            },
            yAxis: {
                title: {
                    text: yAxis
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x +': '+ this.y +tooltip;
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            series: series
        });
    });
    
};