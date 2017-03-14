package com.cyberlink.cosmetic.modules.cyberlink.model;

public enum CyberLinkMemberStatus {

    OK(0), TokenNotExist(1), TokenExpired(2), MemberServiceUnavailable(3), MemberNotExist(
            4), WaitValidate(5), WrongPassWord(6);

    private Integer code;

    private CyberLinkMemberStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

    public static CyberLinkMemberStatus getByCode(Integer code) {
        for (CyberLinkMemberStatus status : CyberLinkMemberStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        return MemberServiceUnavailable;
    }

}
