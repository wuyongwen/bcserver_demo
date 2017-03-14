package com.cyberlink.cosmetic.core.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;

public enum AccountSource {

    Phone {
        public Boolean isValidReference(String shortReference) {
            if (StringUtils.isBlank(shortReference)) {
                return false;
            }
            if (StringUtils.length(shortReference) < 6) {
                return false;
            }
            if (!NumberUtils.isDigits(shortReference)) {
                return false;
            }
            return true;
        }

        public EntryType toEntryType() {
            return EntryType.Phone;
        }
    },

    CyberLink {
        public Boolean isValidReference(String shortReference) {
            return NumberUtils.isDigits(shortReference);
        }

        public EntryType toEntryType() {
            return EntryType.Unknown;
        }
    },

    Facebook {
        public Boolean isValidReference(String shortReference) {
            return NumberUtils.isDigits(shortReference);
        }

        public EntryType toEntryType() {
            return EntryType.Facebook;
        }
    },

    CyberLinkEmail {
        public Boolean isValidReference(String shortReference) {
            return EmailValidator.getInstance().isValid(shortReference);
        }

        public EntryType toEntryType() {
            return EntryType.Email;
        }
    };

    public boolean isPhone() {
        return Phone == this;
    }

    public boolean isCyberLink() {
        return CyberLink == this;
    }

    public boolean isFacebook() {
        return Facebook == this;
    }

    public boolean isCyberLinkEmail() {
        return CyberLinkEmail == this;
    }

    public abstract Boolean isValidReference(String shortReference);

    public abstract EntryType toEntryType();
}
