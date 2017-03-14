package com.cyberlink.core.web.jackson;

public class Views {
    public static class Basic {        
    }
    
    public static class Simple extends Basic {
    }
    
    public static class Public extends Simple {
    }

    public static class Shared extends Public {
    }

    public static class Full extends Shared {
    }
}
