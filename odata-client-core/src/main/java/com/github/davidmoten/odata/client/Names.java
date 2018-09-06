package com.github.davidmoten.odata.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.oasisopen.odata.csdl.v4.Schema;
import org.oasisopen.odata.csdl.v4.TComplexType;
import org.oasisopen.odata.csdl.v4.TEntityType;
import org.oasisopen.odata.csdl.v4.TEnumType;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.Sets;

final class Names {

    private static final Set<String> javaReservedWords = Sets.newHashSet("abstract", "assert", "boolean", "break",
            "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends",
            "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return",
            "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
            "true", "try", "void", "volatile", "while");

    private final Schema schema;
    private final Options options;
    private final File output;

    private final Map<String, String> classNamesFromNamespacedType;

    private Names(Schema schema, Options options) {
        this.schema = schema;
        this.options = options;
        File output = new File(options.outputDirectory());
        Util.deleteDirectory(output);
        output.mkdirs();
        this.output = output;
        this.classNamesFromNamespacedType = createMap(schema, options);
    }

    private Map<String, String> createMap(Schema schema, Options options) {
        Map<String, String> map = new HashMap<>();
        schema.getComplexTypeOrEntityTypeOrTypeDefinition() //
                .stream() //
                .filter(x -> x instanceof TEnumType) //
                .map(x -> ((TEnumType) x)) //
                .forEach(x -> map.put(schema.getNamespace() + "." + x.getName(), getFullClassNameEnum(x.getName())));

        schema.getComplexTypeOrEntityTypeOrTypeDefinition() //
                .stream() //
                .filter(x -> x instanceof TEntityType) //
                .map(x -> ((TEntityType) x)) //
                .forEach(x -> map.put(schema.getNamespace() + "." + x.getName(), getFullClassNameEntity(x.getName())));
        
        schema.getComplexTypeOrEntityTypeOrTypeDefinition() //
        .stream() //
        .filter(x -> x instanceof TComplexType) //
        .map(x -> ((TComplexType) x)) //
        .forEach(x -> map.put(schema.getNamespace() + "." + x.getName(), getFullClassNameComplexType(x.getName())));
        return map;
    }

    static String toSimpleClassName(String name) {
        return upperFirst(name);
    }

    private static String upperFirst(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    }

    private static String lowerFirst(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
    }

    static String toConstant(String name) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return name.replaceAll(regex, replacement).toUpperCase();
    }

    static String toIdentifier(String s) {
        if (javaReservedWords.contains(s)) {
            s = s + "_";
        }
        return lowerFirst(s);
    }

    private static File toDirectory(File base, String pkg) {
        String path = base.getAbsolutePath() + File.separatorChar + pkg.replace('.', File.separatorChar);
        return new File(path);
    }

    public File getDirectoryEntity() {
        return toDirectory(output, options.pkg() + options.packageSuffixEntity());
    }

    public File getDirectoryEnum() {
        return toDirectory(output, options.pkg() + options.packageSuffixEnum());
    }

    public static Names clearOutputDirectoryAndCreate(Schema schema, Options options) {
        Names names = new Names(schema, options);
        names.getDirectoryEntity().mkdirs();
        names.getDirectoryEnum().mkdirs();
        return names;
    }

    public String getPackageEnum() {
        return options.pkg() + options.packageSuffixEnum();
    }

    public String getPackageEntity() {
        return options.pkg() + options.packageSuffixEntity();
    }

    public String getSimpleClassNameEnum(String name) {
        return Names.toSimpleClassName(name);
    }

    public String getSimpleClassNameEntity(String name) {
        return Names.toSimpleClassName(name);
    }

    public String getFullClassNameEnum(String name) {
        return getPackageEnum() + "." + getSimpleClassNameEnum(name);
    }

    public String getFullClassNameEntity(String name) {
        return getPackageEntity() + "." + getSimpleClassNameEntity(name);
    }
    
    private String getFullClassNameComplexType(String name) {
        return getPackageEntity() + "." + getSimpleClassNameEntity(name);
    }

    public File getClassFileEnum(String name) {
        return new File(getDirectoryEnum(), getSimpleClassNameEnum(name) + ".java");
    }

    public File getClassFileEntity(String name) {
        return new File(getDirectoryEntity(), getSimpleClassNameEntity(name) + ".java");
    }

    public String getFullGeneratedClassNameFromNamespacedType(String type) {
        return Preconditions.checkNotNull(classNamesFromNamespacedType.get(type), "class name not found for " + type);
    }
}
