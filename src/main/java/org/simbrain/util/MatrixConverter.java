package org.simbrain.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import smile.math.matrix.Matrix;

/**
 * Save Smile Matrices as base 64 byte-streams from double arrays.
 */
public class MatrixConverter implements Converter {

    @Override
    public boolean canConvert(Class type) {
        return type == Matrix.class;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Matrix matrix =  ((Matrix) source);

        writer.startNode("rows");
        context.convertAnother(matrix.nrow());
        writer.endNode();

        writer.startNode("cols");
        context.convertAnother(matrix.ncol());
        writer.endNode();

        writer.startNode("data");
        double[] flatArray = CollectionUtilsKt.flattenArray(matrix.toArray());
        context.convertAnother(DoubleArrayConverter.arrayToString(flatArray));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        reader.moveDown();
        int rows = Integer.parseInt(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        int cols = Integer.parseInt(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        double[] flatData = DoubleArrayConverter.stringToArray(reader.getValue());
        reader.moveUp();

        return Matrix.of(CollectionUtilsKt.reshape(rows, cols, flatData));
    }

}
