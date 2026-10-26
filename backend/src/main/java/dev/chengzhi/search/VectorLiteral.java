package dev.chengzhi.search;

public final class VectorLiteral {

    private VectorLiteral() {
    }

    public static String toSql(float[] vector) {
        if (vector == null || vector.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder("{");
        for (int index = 0; index < vector.length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(vector[index]);
        }
        return builder.append('}').toString();
    }
}
