

package com.github.newk5.vcmp.javascript.plugin.internals.result;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import java.util.Arrays;
import java.util.Objects;


public abstract class AsyncResult implements ResultBuilder {

    private V8Function callback;
    private Object[] params;
    private boolean maintainCallback;
    public AsyncResult() {
    }

    public AsyncResult(V8Function callback, Object[] params) {
        this.callback = callback;
        this.params = params;
    }

  

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.callback);
        hash = 71 * hash + Arrays.deepHashCode(this.params);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AsyncResult other = (AsyncResult) obj;
        if (!Objects.equals(this.callback, other.callback)) {
            return false;
        }
        if (!Arrays.deepEquals(this.params, other.params)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AsyncResult{" + "callback=" + callback + ", params=" + params + '}';
    }

    public V8Function getCallback() {
        return callback;
    }

    public void setCallback(V8Function callback) {
        this.callback = callback;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    /**
     * @return the maintainCallback
     */
    public boolean isMaintainCallback() {
        return maintainCallback;
    }

    /**
     * @param maintainCallback the maintainCallback to set
     */
    public void setMaintainCallback(boolean maintainCallback) {
        this.maintainCallback = maintainCallback;
    }


    @Override
    public V8Array build() {
        return null;
    }

}
