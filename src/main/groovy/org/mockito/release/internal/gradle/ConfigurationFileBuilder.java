package org.mockito.release.internal.gradle;

import org.mockito.release.internal.util.ArgumentValidation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationFileBuilder {

    private final String extensionName;
    private final Map<String, Object> properties;

    public ConfigurationFileBuilder(String extensionName){
        ArgumentValidation.notNull(extensionName, "extensionName");
        this.extensionName = extensionName;
        this.properties = new LinkedHashMap<String, Object>();
    }

    public ConfigurationFileBuilder withProperty(String key, Object value){
        properties.put(key, value);
        return this;
    }

    public String build(){
        StringBuilder content = new StringBuilder(extensionName + " {\n");

        for(Map.Entry<String, Object> property : properties.entrySet()){
            content.append("\t")
                    .append(property.getKey())
                    .append(" = ")
                    .append(formatValue(property.getValue()))
                    .append("\n");
        }

        return content.append("}\n").toString();
    }

    private String formatValue(Object value) {
        if(value instanceof String){
            return "\"" + value + "\"";
        }
        if(value instanceof Expression){
            return ((Expression) value).expression;
        }
        if(value instanceof List){
            return formatList((List) value);
        }
        if(value instanceof Map){
            return formatMap((Map) value);
        }
        throw new IllegalStateException("format not supported yet");
    }

    private String formatList(List value) {
        StringBuilder result = new StringBuilder("[");
        List elements = value;
        for(Object element : elements){
            result.append(formatValue(element)).append(",");
        }
        if(!elements.isEmpty()){
            result.deleteCharAt(result.length() - 1);
        }
        return result.append("]").toString();
    }

    private String formatMap(Map value) {
        StringBuilder result = new StringBuilder("[");
        Map entries = value;
        for(Map.Entry entry : (Set<Map.Entry>) entries.entrySet()){
            result.append(entry.getKey())
                    .append(":")
                    .append(formatValue(entry.getValue())).append(",");
        }
        if(!entries.isEmpty()){
            result.deleteCharAt(result.length() - 1);
        } else{
            result.append(":");
        }
        return result.append("]").toString();
    }

    public static class Expression {
        private final String expression;

        public Expression(String expression) {
            this.expression = expression;
        }
    }
}
