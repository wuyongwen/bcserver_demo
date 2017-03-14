package com.cyberlink.cosmetic.modules.event.model;

import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class Stores {
    @JsonView(Views.Public.class)
    public String city;
    @JsonView(Views.Public.class)
    public List<Store> stores;
    
    public Stores() {
        
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public static class Store {
        @JsonView(Views.Public.class)
        public String name;
        @JsonView(Views.Public.class)
        public String address;
        
        public Store() {
            
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}