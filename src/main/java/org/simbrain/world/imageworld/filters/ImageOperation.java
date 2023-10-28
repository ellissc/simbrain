package org.simbrain.world.imageworld.filters;

import org.jetbrains.annotations.Nullable;
import org.simbrain.util.propertyeditor.CopyableObject;

import java.awt.image.BufferedImageOp;
import java.util.List;

/**
 * An abstract image operation.
 *
 * @param <O> The type of the image operation
 */
public abstract class ImageOperation<O extends BufferedImageOp> implements CopyableObject {

    /**
     * List of classes for filter menu in property editor
     */
    private static List<Class<? extends CopyableObject>> OP_LIST = List.of(
            IdentityOp.class,
            GrayOp.class,
            ThresholdOp.class
    );
    // TODO: Can later add OffsetOp when use cases are worked out


    @Nullable
    @Override
    public List<Class<? extends CopyableObject>> getTypeList() {
        return OP_LIST;
    }

    abstract O getOp();
}
