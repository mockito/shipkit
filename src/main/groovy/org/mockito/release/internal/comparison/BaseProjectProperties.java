package org.mockito.release.internal.comparison;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

public class BaseProjectProperties {

    private final String group;
    private final String name;

    public BaseProjectProperties(String group, String name){
        notNull(group, "project group", name, "project name");
        this.group = group;
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseProjectProperties that = (BaseProjectProperties) o;

        if (!group.equals(that.group)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BaseProjectProperties{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
