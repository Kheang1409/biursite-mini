package com.biursite.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "query")
public class QueryProperties {
    private int maxPageSize = 100;
    private final Search search = new Search();
    private final Features features = new Features();

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public Search getSearch() {
        return search;
    }

    public Features getFeatures() {
        return features;
    }

    public static class Search {
        private int maxLength = 200;

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static class Features {
        private boolean useMaterializedView = false;
        private boolean useAsyncProjections = false;

        public boolean isUseMaterializedView() {
            return useMaterializedView;
        }

        public void setUseMaterializedView(boolean useMaterializedView) {
            this.useMaterializedView = useMaterializedView;
        }

        public boolean isUseAsyncProjections() {
            return useAsyncProjections;
        }

        public void setUseAsyncProjections(boolean useAsyncProjections) {
            this.useAsyncProjections = useAsyncProjections;
        }
    }
}
