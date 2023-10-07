package org.simbrain.custom_sims.simulations.patterns_of_activity;

import org.simbrain.custom_sims.Simulation;
import org.simbrain.network.NetworkComponent;
import org.simbrain.network.connections.ConnectionStrategy;
import org.simbrain.network.connections.RadialGaussian;
import org.simbrain.network.connections.Sparse;
import org.simbrain.network.core.Network;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.NeuronUpdateRule;
import org.simbrain.network.core.SynapseGroup2;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.layouts.HexagonalGridLayout;
import org.simbrain.network.layouts.LineLayout;
import org.simbrain.network.neuron_update_rules.BinaryRule;
import org.simbrain.network.neuron_update_rules.DecayRule;
import org.simbrain.network.neuron_update_rules.KuramotoRule;
import org.simbrain.plot.projection.ProjectionComponent;
import org.simbrain.util.SimbrainConstants;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Producer;
import org.simbrain.workspace.gui.SimbrainDesktop;
import org.simbrain.world.odorworld.OdorWorldComponent;
import org.simbrain.world.odorworld.entities.EntityType;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;
import org.simbrain.world.odorworld.sensors.ObjectSensor;
import org.simbrain.world.odorworld.sensors.Sensor;

import java.util.ArrayList;
import java.util.List;

import static org.simbrain.network.connections.RadialGaussianKt.*;
import static org.simbrain.network.core.NetworkUtilsKt.addNeuronGroup;
import static org.simbrain.network.core.NetworkUtilsKt.connect;


/**
 * Simulate a set of oscillatory brain networks and display their projected
 * activity when exposed to inputs in a simple 2d world.
 */
public class ModularOscillatoryNetwork extends Simulation {

    // References
    NetworkComponent nc;
    Network net;
    NeuronGroup sensory, motor, inputGroup;
    OdorWorldEntity mouse;
    List<OdorWorldEntity> worldEntities = new ArrayList<>();

    private int dispersion = 140;

    @Override
    public void run() {

        // Clear workspace
        sim.getWorkspace().clearWorkspace();

        // Set up world
        setUpWorld();

        // Set up network
        setUpNetwork();

        // Set up separate projections for each module
        addProjection(inputGroup, 8, 304, .01);
        addProjection(sensory, 359, 304, .1);
        addProjection(motor, 706, 304, .5);

        // Set up workspace updating
        //sim.getWorkspace().addUpdateAction((new ColorPlotKuramoto(this)));

    }

    private void setUpNetwork() {

        // Set up network
        nc = sim.addNetwork(9,8,581,297,
            "Patterns of Activity");
        net = nc.getNetwork();

        // Sensory network
        sensory = addModule(-115, 10, 49, "Sensory", new DecayRule());
        SynapseGroup2 recSensory = connectRadialGaussian(sensory,sensory);
        recSensory.setLabel("Recurrent Sensory");

        // Motor Network
        motor = addModule(322, 10, 16, "Motor", new KuramotoRule());
        SynapseGroup2 recMotor = connectRadialGaussian(motor, motor);
        recMotor.setLabel("Recurrent Motor");

        // Sensori-Motor Connection
        connectModules(sensory, motor, .3, .5);

        // Input Network
        inputGroup = addInputGroup(-385, 107);

    }

    private NeuronGroup addInputGroup(int x, int y) {

        // Alternate form would be based on vectors
        NeuronGroup ng = addNeuronGroup(net, x, y, mouse.getSensors().size());
        ng.setLayout(new LineLayout(LineLayout.LineOrientation.VERTICAL));
        ng.applyLayout(-5, -85);
        ng.setLabel("Object Sensors");
        int i = 0;
        for (Sensor sensor : mouse.getSensors()) {
            Neuron neuron = ng.getNeuron(i++);
            neuron.setLabel(sensor.getLabel());
            neuron.setClamped(true);
            sim.couple(sensor, neuron);
        }

        // Hard coded for two input neurons
        Neuron neuron1 = ng.getNeuron(0);
        Neuron neuron2 = ng.getNeuron(1);

        // Make spatial connections to sensory group
        double yEdge = sensory.getCenterY();
        for (int j = 0; j < sensory.getNeuronList().size(); j++) {
            Neuron tarNeuron = sensory.getNeuronList().get(j);
            double yloc = tarNeuron.getY();
            if (yloc < yEdge) {
                connect(neuron1, tarNeuron,1);
            } else {
                connect(neuron2, tarNeuron,1);
            }
        }

        return ng;

    }

    private NeuronGroup addBinaryModule(int x, int y, int numNeurons, String name) {
        NeuronGroup ng = addNeuronGroup(net, x, y, numNeurons);
        BinaryRule rule = new BinaryRule();
        ng.setNeuronType(rule);
        HexagonalGridLayout.layoutNeurons(ng.getNeuronList(), 40, 40);
        ng.setLocation(x, y);
        ng.setLabel(name);
        return ng;
    }

    private NeuronGroup addModule(int x, int y, int numNeurons, String name, NeuronUpdateRule rule) {
        NeuronGroup ng = addNeuronGroup(net, x, y, numNeurons);
        //KuramotoRule rule = new KuramotoRule();
        //NakaRushtonRule rule = new NakaRushtonRule();
        //rule.setNaturalFrequency(.1);
        ng.setNeuronType(rule);
        for (Neuron neuron : ng.getNeuronList()) {
            if (Math.random() < .5) {
                neuron.setPolarity(SimbrainConstants.Polarity.EXCITATORY);
            } else {
                neuron.setPolarity(SimbrainConstants.Polarity.INHIBITORY);
            }
        }
        HexagonalGridLayout.layoutNeurons(ng.getNeuronList(), 40, 40);
        ng.setLocation(x, y);
        ng.setLabel(name);
        return ng;
    }

    private SynapseGroup2 connectRadialGaussian(NeuronGroup sourceNg, NeuronGroup targetNg) {
        ConnectionStrategy radialConnection = new RadialGaussian(DEFAULT_EE_CONST * 1, DEFAULT_EI_CONST * 2,
            DEFAULT_IE_CONST * 3, DEFAULT_II_CONST * 0, .25, 50.0);
        SynapseGroup2 sg = new SynapseGroup2(sourceNg, targetNg, radialConnection);
        net.addNetworkModelAsync(sg);
        sg.setDisplaySynapses(false);
        return sg;
    }

    private SynapseGroup2 connectModules(NeuronGroup sourceNg, NeuronGroup targetNg, double density, double exRatio) {
        Sparse sparse = new Sparse(density);
        SynapseGroup2 sg = new SynapseGroup2(sourceNg, targetNg);
        // TODO!
        // , exRatio)
//        sparse.connectNeurons(sg);
        net.addNetworkModelAsync(sg);
        sg.setDisplaySynapses(false);
        return sg;
    }

    private void setUpWorld() {

        OdorWorldComponent oc = sim.addOdorWorld(590,9,505,296, "World");

        // Mouse
        mouse = oc.getWorld().addEntity(187, 113, EntityType.MOUSE);

        // Objects
        OdorWorldEntity cheese = oc.getWorld().addEntity(315, 31, EntityType.SWISS);
        worldEntities.add(cheese);
        OdorWorldEntity flower = oc.getWorld().addEntity(41, 31, EntityType.FLOWER);
        flower.getSmellSource().setDispersion(dispersion);
        worldEntities.add(flower);

        // Add sensors
        for (OdorWorldEntity entity : worldEntities) {
            ObjectSensor sensor = new ObjectSensor(entity.getEntityType());
            sensor.getDecayFunction().setDispersion(dispersion);
            mouse.addSensor(sensor);
        }
    }

    private void addProjection(NeuronGroup toPlot, int x, int y, double tolerance) {

        // Create projection component
        ProjectionComponent pc = sim.addProjectionPlot(x, y, 362, 320, toPlot.getLabel());
        pc.getProjector().init();
        pc.getProjector().setTolerance(tolerance);
        //plot.getProjector().useColorManager = false;

        // Coupling
        Producer inputProducer = sim.getProducer(toPlot, "getActivations");
        Consumer plotConsumer = sim.getConsumer(pc, "addPoint");
        sim.couple(inputProducer, plotConsumer);

        // Text of nearest world object to projection plot current dot
        Producer currentObject = sim.getProducer(mouse, "getNearbyObjects");
        Consumer plotText = sim.getConsumer(pc, "setLabel");
        sim.couple(currentObject, plotText);

    }

    public ModularOscillatoryNetwork(SimbrainDesktop desktop) {
        super(desktop);
    }

    public ModularOscillatoryNetwork() {
        super();
    }

    private String getSubmenuName() {
        return "Cognitive Maps";
    }

    @Override
    public String getName() {
        return "Modular Oscillatory Network";
    }

    @Override
    public ModularOscillatoryNetwork instantiate(SimbrainDesktop desktop) {
        return new ModularOscillatoryNetwork(desktop);
    }
}