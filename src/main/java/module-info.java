module net.hirlab.ktsignage {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;
    requires kotlinx.coroutines.core.jvm;
    requires kotlinx.coroutines.javafx;

    requires tornadofx;

    requires java.xml;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.json;
    requires com.google.guice;
    requires okhttp3;

    opens net.hirlab.ktsignage to tornadofx;
    exports net.hirlab.ktsignage;
}