package com.cyberlink.cosmetic.core.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;

public enum EntryType {

    Phone {
        public String toShortItem(String oriEntryItem) {
            String t = toTrimmedItem(oriEntryItem);
            String str = (t.length() <= 6 ? t : t.substring(t.length() - 6));
            return str;
        }

        public String toTrimmedItem(String oriEntryItem) {
            String t = oriEntryItem;
            if (t.indexOf('#') >= 0) {
                t = t.substring(0, t.indexOf('#'));
            }
            t = t.replaceAll("[\\D]", "");
            return t;
        }
        
        public Boolean isValidItem(String entryItem) {
            if (StringUtils.isBlank(entryItem)) {
                return false;
            }
            if (StringUtils.length(entryItem) < 6) {
                return false;
            }
            if (!NumberUtils.isDigits(entryItem)) {
                return false;
            }
            return true;
        }

        public List<AccountSource> toAccountSources() {
            List<AccountSource> l = new ArrayList<AccountSource>();
            l.add(AccountSource.Phone);
            return l;
        }
    },

    Email {
        public String toShortItem(String oriEntryItem) {
            return oriEntryItem;
        }
        
        public String toTrimmedItem(String oriEntryItem) {
            return oriEntryItem;
        }

        public Boolean isValidItem(String entryItem) {
            return EmailValidator.getInstance().isValid(entryItem);
        }

        public List<AccountSource> toAccountSources() {
            List<AccountSource> l = new ArrayList<AccountSource>();
            l.add(AccountSource.CyberLinkEmail);
            return l;
        }
    },

    Facebook {
        public String toShortItem(String oriEntryItem) {
            return oriEntryItem;
        }

        public String toTrimmedItem(String oriEntryItem) {
            return oriEntryItem;
        }
        
        public Boolean isValidItem(String entryItem) {
            return NumberUtils.isDigits(entryItem);
        }

        public List<AccountSource> toAccountSources() {
            List<AccountSource> l = new ArrayList<AccountSource>();
            l.add(AccountSource.Facebook);
            return l;
        }
    },

    Unknown {
        public String toShortItem(String oriEntryItem) {
            return oriEntryItem;
        }
        
        public String toTrimmedItem(String oriEntryItem) {
            return oriEntryItem;
        }        

        public Boolean isValidItem(String entryItem) {
            return true;
        }

        public List<AccountSource> toAccountSources() {
            return new ArrayList<AccountSource>();
        }
    };

    public abstract String toShortItem(String oriEntryItem);
    
    public abstract String toTrimmedItem(String oriEntryItem);

    public abstract Boolean isValidItem(String entryItem);

    public abstract List<AccountSource> toAccountSources();

}
