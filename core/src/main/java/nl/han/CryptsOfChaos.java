package nl.han;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.java.Log;
import nl.han.client.INetwork;
import nl.han.client.Network;
import nl.han.compiler.CompilerController;
import nl.han.compiler.ICompiler;
import nl.han.interfaces.IProfile;
import nl.han.interfaces.IUI;
import nl.han.pathfinding.AStar;
import nl.han.pathfinding.IPathFindingAlgorithm;
import nl.han.modules.Binding;
import nl.han.modules.ModuleFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;

@Log
public class CryptsOfChaos {
    public static final Random seededRandom = new Random(0L);

    public static void main(String[] args) {
        try {
            LogManager.getLogManager()
                    .readConfiguration(CryptsOfChaos.class.getClassLoader().getResourceAsStream("coc.properties"));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        ModuleFactory moduleFactory = new ModuleFactory();
        moduleFactory.add(new Binding<>(IUI.class, UserInterface.class))
                .add(new Binding<>(IPathFindingAlgorithm.class, AStar.class))
                .add(new Binding<>(ICompiler.class, CompilerController.class))
                .add(new Binding<>(INetwork.class, Network.class))
                .add(new Binding<>(IProfile.class, ProfileService.class))
                .add(new Binding<>(ISQLUtils.class, HSQLDBUtils.class));

        Injector injector = Guice.createInjector(moduleFactory.createModules());
        GameManager gameManager = injector.getInstance(GameManager.class);
        gameManager.startUI();
    }
}
