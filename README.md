# Restaurant


----------
### 2016.11.29

[Fix]

 - 饭店端的`确认送达`功能移动到司机端。
 - 在司机端，修改在`实时位置`页面上**司机到饭店路线**的颜色透明度。
 - 在司机端，修复因实时更新司机位置（经纬度）而出现**多条司机到饭店路线**的问题。

[Add]

 - 司机端，新增`确认送达`功能

[Update]

 - 优化在`实时位置`页面的订单状态显示。
 


----------


### 2016.11.30

[Add]

 - 服务端，新增获取订单数量的API，`Order/getOrderCount`
 - 客户端新增轮询服务，监听服务端数据数目变化，有数据更新时会以Notification实时通知客户端。


----------


### 2016.12.1

[Fix]

 - 服务端，修改`Order/getOrderCount`API，修复清空数据表表中无数据时，轮询服务Crash的问题。


# 演示

#### Admin 饭店端
![images](https://github.com/gaoyuyu/Restaurant/raw/master/captures/admin.gif)

#### Driver 司机端
![images](https://github.com/gaoyuyu/Restaurant/raw/master/captures/driver.gif)

#### 饭店端下单，司机端接收Notification通知(各种Android机型接受通知存在差异)
Google Android 6.0
![images](https://github.com/gaoyuyu/Restaurant/raw/master/captures/notification.gif)
MIUI8
![images](https://github.com/gaoyuyu/Restaurant/raw/master/captures/notification_real.gif)
#### 饭店端查看订单
![images](https://github.com/gaoyuyu/Restaurant/raw/master/captures/admin_check_order.gif)

 