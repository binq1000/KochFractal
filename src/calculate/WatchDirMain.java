/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erik
 */
public class WatchDirMain {

    public WatchDirMain(String directory, boolean recursive, KochManager km) {
        Path dir = Paths.get(directory);

        try {
            // create WatchDirRunnable object to watch the given directory (and possibly recursive)
            WatchDirRunnable watcher = new WatchDirRunnable(dir, recursive, km);
            // create Thread and start watching
            new Thread(watcher).start();

        } catch (IOException ex) {
            Logger.getLogger(WatchDirMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }
    
}
