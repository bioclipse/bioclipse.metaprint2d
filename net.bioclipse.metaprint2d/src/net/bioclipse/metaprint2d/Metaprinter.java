/* *****************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.metaprint2d;

 import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import net.sf.metaprint2d.MetaPrintCalculator;
import net.sf.metaprint2d.MetaPrintConfiguration;
import net.sf.metaprint2d.MetaPrintResult;
import net.sf.metaprint2d.data.MemoryDataSource;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;



public class Metaprinter {

	private static final Logger logger = Logger.getLogger(Metaprinter.class);

	public static final Color RED_COLOR=new Color(200,70,70);
	public static final Color GREEN_COLOR=new Color(00,200,80);
	public static final Color AMBER_COLOR=new Color(255,191,0);

	public static final Color BLACK_COLOR = new Color(150,150,150);
	public static final Color WHITE_COLOR = new Color(255,255,255);

//	private static final String DATAFILE_ALL = "/data/metab2008.1-all.bin";
//	private static final String DATAFILE_DOG = "/data/metab2008.1-dog.bin";
//	private static final String DATAFILE_HUMAN = "/data/metab2008.1-human.bin";
//	private static final String DATAFILE_RAT = "/data/metab2008.1-rat.bin";

	 private static final String DATAFILE_ALL = "/data/EntireDB2005AllFields.rdf.bin";
//	  private static final String DATAFILE_DOG = "/data/metaprint2d-metab2008_1-dog.bin";
//	  private static final String DATAFILE_HUMAN = "/data/metaprint2d-metab2008_1-human.bin";
//	  private static final String DATAFILE_RAT = "/data/metaprint2d-metab2008_1-rat.bin";

	//Cache calculator
	private static MetaPrintCalculator metaprint = null;

	static String operator;
	static MemoryDataSource memoryDAO;
	protected static MetaPrintConfiguration storedConfig;
	private static Species species;
  private static boolean firstDBLoad=true;
	
	static{

		//Set default values
		setOperator("DEFAULT");
		setSpecies("all");
//		firstDBLoad=true;
	}

	/**
	 * Load and cache Metaprint calculator
	 * @param config
	 * @return
	 */
	public static MetaPrintCalculator getMetaprint() throws UnsupportedOperationException{

		if (metaprint!=null) return metaprint;

		final IRunnableWithProgress pr=new IRunnableWithProgress(){
			public void run( IProgressMonitor monitor ){

			monitor.beginTask( "Calculating MetaPrint2D for " + getSpecies(), 3 );
			monitor.worked( 1 );

			//IF there is a metaprintjob running, wait for it
			IJobManager jobMan = Job.getJobManager();
			Job[] runningJobs = jobMan.find(MetaPrint2DJob.METAPRINT_JOB_FAMIlY); 
			if (runningJobs.length >0){

				monitor.subTask("Waiting for database to load...");
				
				for (int i=0; i< runningJobs.length;i++){
					MetaPrint2DJob job=(MetaPrint2DJob)runningJobs[i];
					try {
						logger.debug("Waiting for m2d job: " + job.getName());
						runningJobs[i].join();
					} catch (InterruptedException e) {
						logger.debug("job: " + runningJobs[i].getName() + " was interrupted");
					}
				}
				logger.debug("All m2d jobs finished. Continuing with calculation.");
			}

			monitor.worked( 1 );


			if (storedConfig==null)
				throw new UnsupportedOperationException("MetaPrint2D config not " +
						"loaded. Operator: " + getOperator());

			if (memoryDAO==null)
				throw new UnsupportedOperationException("MetaPrint2D database not loaded.");

			monitor.subTask("Setting up MetaPrint2D with database: " + getSpecies() + 
					" and operator: " + getOperator());

			MetaPrintCalculator tmpMeta =
				new MetaPrintCalculator(storedConfig, memoryDAO);

			Metaprinter.setMetaprint(tmpMeta);

			monitor.worked( 1 );
			monitor.done();

			return;

		}

	};

		//Needs to be run in UI thread
		Display.getDefault().syncExec(new Runnable(){

			public void run() {
				try {
					PlatformUI.getWorkbench().getProgressService().run(false,false,pr);
				} catch (InvocationTargetException e) {
					logger.debug("Metaprint2D job suffered an error: " + e.getMessage());
					e.printStackTrace();
				} catch (InterruptedException e) {
					logger.debug("Metaprint2D job was interrupted: " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		});

	return metaprint;

}

    
    /**
     * We always create a new config as this is cheap.
     * @return
     */
    protected static void createMetaprintConfig() throws IllegalArgumentException{
        
        String poperator = getOperator();
        
        if ("STRICT".equals(poperator)) {
            storedConfig = MetaPrintConfiguration.createStrictConfiguration();
        } else if ("DEFAULT".equals(poperator)) {
        	storedConfig = MetaPrintConfiguration.createDefaultConfiguration();
        } else if ("LOOSE".equals(poperator)) {
        	storedConfig = MetaPrintConfiguration.createLooseConfiguration();
        } else {
        	throw new IllegalArgumentException("No similarity operator");
        }
        
        logger.debug("New operator set: " + poperator);

	}
    
    protected static void setMetaprint( MetaPrintCalculator tmpMeta ) {
        metaprint=tmpMeta;
    }

    public static Species getSpecies() {
        if (species!=null) return species;

        logger.debug("Setting default metaprint2d database: ALL");
        setSpecies("all");
        return Species.ALL;

    }

    private static File getDataFile(Species species) {
    	Bundle bundle = Platform.getBundle( Activator.PLUGIN_ID );
    	URL url=null;
    	switch (species) {
    	case ALL:
    		url = FileLocator.find( bundle, new Path(DATAFILE_ALL),null );
    		break;
//    	case DOG: 
//    		url = FileLocator.find( bundle, new Path(DATAFILE_DOG),null );
//    		break;
//    	case HUMAN: 
//    		url = FileLocator.find( bundle, new Path(DATAFILE_HUMAN),null );
//    		break;
//    	case RAT: 
//    		url = FileLocator.find( bundle, new Path(DATAFILE_RAT),null );
//    		break;
        }
    	
		try {
			return new File(FileLocator.toFileURL(url).getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

        throw new RuntimeException("Unknown species: " + species);
    }

    
    //This is the metaprint cutoff
    public static Color getColorByMetprint( MetaPrintResult res ) {

        double ratio=res.getNormalisedRatio();

        if (ratio>0.66){
            return Metaprinter.RED_COLOR;
        }
        else if (0.66>=ratio && ratio>0.33){
            return Metaprinter.AMBER_COLOR;
        }
        else if (0.33>=ratio && ratio>0.15){
            return Metaprinter.GREEN_COLOR;
        }
        return null;
    }

    public static void setSpecies( String parm ) {

        if (parm.equalsIgnoreCase( "all")){
        	if (species==Species.ALL){
        		//No change
        		return;
        	}
        	species=Species.ALL;
        }
        else if (parm.equalsIgnoreCase( "dog")){
        	if (species==Species.DOG){
        		//No change
        		return;
        	}
        	species=Species.DOG;
        }
        else if (parm.equalsIgnoreCase( "human")){
        	if (species==Species.HUMAN){
        		//No change
        		return;
        	}
        	species=Species.HUMAN;
        }
        else if (parm.equalsIgnoreCase( "rat")){
        	if (species==Species.RAT){
        		//No change
        		return;
        	}
        	species=Species.RAT;
        }
        
        //Fire up job to load databases
        loadMetaprintDatabase();
    	metaprint=null;	//Force recreation on next call

    }

    
    /**
     * Rereads the dao from file. If there is a job doing this already, abort it
     * if not reading the same species.
     */
	private static void loadMetaprintDatabase() {

		//================================================
		//Join or cancel ongoing jobs
		//================================================
		IJobManager jobMan = Job.getJobManager();
		Job[] runningJobs = jobMan.find(MetaPrint2DJob.METAPRINT_JOB_FAMIlY); 
		if (runningJobs.length >0){
			for (int i=0; i< runningJobs.length;i++){
				
				MetaPrint2DJob job=(MetaPrint2DJob)runningJobs[i];
				if (job.getType().equals(MetaPrint2DJob.METAPRINT_JOB_DBTYPE)){
					//This is of db type, so abort it and start new
					logger.debug("Canceling m2d job: " + job.getName());
					job.cancel();
				}else{
					try {
						logger.debug("Waiting for m2d job: " + job.getName());
						runningJobs[i].join();
					} catch (InterruptedException e) {
						logger.debug("job: " + runningJobs[i].getName() + " was interrupted");
					}
				}
			}
		}

		//================================================
		// Ok, we are done with waiting or canceling jobs.
		// So set up new job to create and store new dao
		//================================================
    	Job loadDBJob=new MetaPrint2DJob("Loading metaprint database: " + getSpecies().name()){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask( "Loading data file: " + getSpecies(), 3 );
                monitor.worked( 1 );
                	
                try {
					memoryDAO= MemoryDataSource.loadBinFile(getDataFile(getSpecies()));
                monitor.worked( 1 );
                monitor.done();

                logger.debug("New metaprint database loaded: " + getSpecies());

                return Status.OK_STATUS;
				} catch (IOException e) {
					
	                logger.error("Could not load metaprint database: " + getSpecies());
	                return new Status(Status.ERROR, Activator.PLUGIN_ID, 
	                		"Could not load MetaPrint2D database " +
	                		"for species: " + getSpecies()){};
				}
			}
    		
			//Indicate that this is a DB job
			@Override
			public String getType() {
				return METAPRINT_JOB_DBTYPE;
			}
    		
    	};

    	//If this is the first job, this is not user-started
    	loadDBJob.setUser(!firstDBLoad);
    	loadDBJob.schedule();
      if (firstDBLoad)
          firstDBLoad=false;

	}

    
    
    
    public static void setOperator( String parm ) {

    	//If no change, return
    	if (operator!=null && operator.equals(parm)) return;
    	
    	operator=parm;
    	createMetaprintConfig();
    	metaprint=null;	//Force recreation on next call
    }

    /**
     * Get operator from store, DEFAULT if none stored.
     * @return
     */
    public static String getOperator() {

        if (operator!=null)
            return operator;
        return "DEFAULT";
    }



}
