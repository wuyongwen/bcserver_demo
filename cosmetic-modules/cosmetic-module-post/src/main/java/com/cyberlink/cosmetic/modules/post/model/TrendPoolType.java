package com.cyberlink.cosmetic.modules.post.model;

public enum TrendPoolType { 
        TGen(1, 10000L, 0D, 0L, 0L, 0L, "tg"),
        TCat(1, 10000L, 0D, 0L, 0L, 0L, "tc"),
        SGen(2, 10000L, 0.5D, 2000L, 3L, 20L, "sg"), 
        SGenCat(2, 10000L, 0.25D, 1500L, 3L, 20L, "sg_ca"), 
        SCat(2, 10000L, 0.25D, 1500L, 3L, 20L, "s_ca");

        final private int group;
        final private Long maxPoolSize;
        final private Double topRatio;
        final private Long updateMax;
        final private String shortForm;
        final private Long cursorStep;
        final private Long shuffleCount;
        
        TrendPoolType(int group, Long maxPoolSize, Double topRatio, Long updateMax, 
                Long cursorStep, Long shuffleCount, String shortForm) {
            this.group = group;
            this.maxPoolSize = maxPoolSize;
            this.topRatio = topRatio;
            this.updateMax = updateMax;
            this.shortForm = shortForm;
            this.cursorStep = cursorStep;
            this.shuffleCount = shuffleCount;
        }
        
        public int getGroup() {
            return group;
        }
        
        public Long getMaxPoolSize() {
            return maxPoolSize;
        }

        public Double getTopRatio() {
            return topRatio;
        }

        public Long getUpdateMax() {
            return updateMax;
        }

        public String getShortForm() {
            return shortForm;
        }
        
        public Long getCursorStep() {
            return cursorStep;
        }

        public Long getShuffleCount() {
            return shuffleCount;
        }

        static public TrendPoolType getFromShortForm(String sf) {
            switch(sf) {
            case "tg" :
                return TGen;
            case "tc" :
                return TCat;
            case "sg" :
                return SGen;
            case "sg_ca" :
                return SGenCat;
            case "s_ca" :
                return SCat;
                default:
                    return null;
            }
        }
    }