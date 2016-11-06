package com.gaoyy.restaurant.bean;


public class Order
{
    private String id;
    private String dispatcher;
    private String receiver;
    private String customer_phone;
    private String customer_address;
    private String price;
    private String remark;
    private String status;
    private String create_time;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getDispatcher()
    {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getCustomer_phone()
    {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone)
    {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_address()
    {
        return customer_address;
    }

    public void setCustomer_address(String customer_address)
    {
        this.customer_address = customer_address;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCreate_time()
    {
        return create_time;
    }

    public void setCreate_time(String create_time)
    {
        this.create_time = create_time;
    }

    @Override
    public String toString()
    {
        return "Order{" +
                "id='" + id + '\'' +
                ", dispatcher='" + dispatcher + '\'' +
                ", receiver='" + receiver + '\'' +
                ", customer_phone='" + customer_phone + '\'' +
                ", customer_address='" + customer_address + '\'' +
                ", price='" + price + '\'' +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
