package org.simbrain.plot.pixelplot;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.simbrain.workspace.AttributeContainer;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.world.imageworld.serialization.BufferedImageConverter;
import org.simbrain.world.imageworld.serialization.CouplingArrayConverter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The interface between pixel display world and the desktop level.
 * Manages couplings and persistence.
 */
public class PixelPlotComponent extends WorkspaceComponent {

    /**
     * The image world this component displays.
     */
    private PixelPlot pixelPlot = new PixelPlot();

    /**
     * Create an Image World Component from a Image World.
     */
    public PixelPlotComponent(String title) {
        super(title);
    }

    /**
     * Deserialize an ImageAlbumComponent.
     */
    public PixelPlotComponent(String name, PixelPlot matrix) {
        super(name);
    }

    /**
     * Open a saved ImageWorldComponent from an XML input stream.
     *
     * @param input  The input stream to read.
     * @param name   The name of the new world component.
     * @param format The format of the input stream. Should be xml.
     * @return A deserialized ImageWorldComponent.
     */
    public static PixelPlotComponent open(InputStream input, String name, String format) {
        PixelPlot matrix = (PixelPlot) getXStream().fromXML(input);
        return new PixelPlotComponent(name, matrix);
    }

    @Override
    public List<AttributeContainer> getAttributeContainers() {
        List<AttributeContainer> containers = new ArrayList<>();
        containers.add(pixelPlot);
        return containers;
    }

    /**
     * Create an xstream from this class.
     */
    public static XStream getXStream() {
        XStream stream = new XStream(new DomDriver());
        stream.registerConverter(new BufferedImageConverter());
        stream.registerConverter(new CouplingArrayConverter());
        return stream;
    }

    @Override
    public void save(OutputStream output, String format) {
        getXStream().toXML(pixelPlot, output);
    }

    public PixelPlot getPixelPlot() {
        return pixelPlot;
    }
}
