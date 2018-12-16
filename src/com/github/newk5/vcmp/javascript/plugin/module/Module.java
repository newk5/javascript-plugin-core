
package com.github.newk5.vcmp.javascript.plugin.module;

import org.pf4j.ExtensionPoint;


public interface Module extends ExtensionPoint {

    void inject();

    String javascript();
 
}
