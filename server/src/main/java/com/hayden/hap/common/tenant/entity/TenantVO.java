package com.hayden.hap.common.tenant.entity;

import com.hayden.hap.common.common.entity.BaseVO;

public class TenantVO extends BaseVO {
    private static final long serialVersionUID = 1L;

    private Long tenantpk;

    private String tenantcode;

    private String tenantname;

    private Integer isenable;

    private String tenant_extcode;

    private Integer isdeploy;

    private String tenant_address;

    private String contact;

    private String tel;

    private Integer tenant_size;

    private String industry;

    private String context;

    private String channel;

    private Integer usersize;

    private Integer capacity;

    private String adminuser;

    private String adminusername;

    private String adminInitPassword;

    private String regcode;

    private Integer isenabledreg;

    private Integer is_allow_trial;

    private Integer is_experience;

    private Integer deploystatus;

    private String tenant_logo_img;

    public TenantVO() {
        super("SY_TENANT");
    }

    public Long getTenantpk() {
        return tenantpk;
    }

    public void setTenantpk(Long tenantpk) {
        this.tenantpk = tenantpk;
    }

    public String getTenantcode() {
        return tenantcode;
    }

    public void setTenantcode(String tenantcode) {
        this.tenantcode = tenantcode == null ? null : tenantcode.trim();
    }

    public String getTenantname() {
        return tenantname;
    }

    public void setTenantname(String tenantname) {
        this.tenantname = tenantname == null ? null : tenantname.trim();
    }

    public Integer getIsenable() {
        return isenable;
    }

    public void setIsenable(Integer isenable) {
        this.isenable = isenable;
    }

    public String getTenant_extcode() {
        return tenant_extcode;
    }

    public void setTenant_extcode(String tenant_extcode) {
        this.tenant_extcode = tenant_extcode == null ? null : tenant_extcode.trim();
    }

    public Integer getIsdeploy() {
        return isdeploy;
    }

    public void setIsdeploy(Integer isdeploy) {
        this.isdeploy = isdeploy;
    }

    public String getTenant_address() {
        return tenant_address;
    }

    public void setTenant_address(String tenant_address) {
        this.tenant_address = tenant_address == null ? null : tenant_address.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }


    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry == null ? null : industry.trim();
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public Integer getUsersize() {
        return usersize;
    }

    public void setUsersize(Integer usersize) {
        this.usersize = usersize;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getAdminuser() {
        return adminuser;
    }

    public void setAdminuser(String adminuser) {
        this.adminuser = adminuser == null ? null : adminuser.trim();
    }

    public String getAdminusername() {
        return adminusername;
    }

    public void setAdminusername(String adminusername) {
        this.adminusername = adminusername == null ? null : adminusername.trim();
    }

    public String getRegcode() {
        return regcode;
    }

    public void setRegcode(String regcode) {
        this.regcode = regcode;
    }

    public Integer getIsenabledreg() {
        return isenabledreg;
    }

    public void setIsenabledreg(Integer isenabledreg) {
        this.isenabledreg = isenabledreg;
    }

    public Integer getTenant_size() {
        return tenant_size;
    }

    public void setTenant_size(Integer tenant_size) {
        this.tenant_size = tenant_size;
    }

    public Integer getIs_allow_trial() {
        return is_allow_trial;
    }

    public void setIs_allow_trial(Integer is_allow_trial) {
        this.is_allow_trial = is_allow_trial;
    }

    public String getAdminInitPassword() {
        return adminInitPassword;
    }

    public void setAdminInitPassword(String adminInitPassword) {
        this.adminInitPassword = adminInitPassword;
    }

    public Integer getIs_experience() {
        return is_experience;
    }

    public void setIs_experience(Integer is_experience) {
        this.is_experience = is_experience;
    }

    public Integer getDeploystatus() {
        return deploystatus;
    }

    public void setDeploystatus(Integer deploystatus) {
        this.deploystatus = deploystatus;
    }

    public String getTenant_logo_img() {
        return tenant_logo_img;
    }

    public void setTenant_logo_img(String tenant_logo_img) {
        this.tenant_logo_img = tenant_logo_img;
    }
}