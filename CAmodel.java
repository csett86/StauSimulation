import java.awt.*;
import java.applet.*;
import java.util.*;

///////////////////////////////////////////////////////////
// Application of the standard CA Nagel Schreckenberg model, VDR, T² and Fukui-Ishibashi
// Simulation of traffic flow
///////////////////////////////////////////////////////////

// B. Eisenblätter, L. Neubert and J. Wahle 1998
// C. Settgast 2005/2006
// last modified 23/01/2006

public class CAmodel extends Applet implements Runnable{  
 
  // language for displaying
  String used_language;
  public int language;
  public final int MAXSPEED = 8;

  // Init values of the scrollbars
  private final int MAXSPEED_INIT = 8;
  private final int DENS_INIT = 20;
  private final int SPEED_INIT = 100;

  // Init values of the deceleration probabilities
  private final double P_DEC_INC_STS = 0.5;
  private final double P_DEC_INC_TSQR = 0.5;
  private final double P_DEC = 0.2;

  // Dinstance between two updates of the diagrams
  private final int UPDATE_DIAGRAM = 10;
  
  // old values for comparision
  private int maxspeed_old = 0;
  private int density_old = 0;
  private int simspeed_old = 1;

  // The panel for displaying everything
  private Panel p;

  // Labels of the scrollbars
  private Label label_maxspeed_name;
  private Label label_density_name;
  private Label label_simspeed_name;
  private Label label_maxspeed;
  private Label label_density;
  private Label label_simspeed;
  
  // The scrollbars themselves
  private Scrollbar scrollbar_maxspeed;
  private Scrollbar scrollbar_density;
  private Scrollbar scrollbar_simspeed;

  // Two buttons
  private Button clear;
  private Button vdr;

  // The pull-down-menu
  private Choice model;

  // The thread
  private Thread runner;
  
  // The canvas for the painting
  private RoadCanvas canvas;

  // Different types of the CA model
  // 0=Standard
  // 1=T²
  // 2=VDR
  // 3=Fukui-Ishibashi
  public int modeltype;

  // The message boxes for the p_dec-tuning
  private MessageBox vdr_box_0;
  private MessageBox vdr_box_1;
  private MessageBox vdr_box_2;
  private MessageBox vdr_box_3;

  // The several deceleration prob's
  private double[] p_dec;
  public double global_p_dec;
  private double global_p_dec_inc_sts;
  private double global_p_dec_inc_tsqr;
  private int maxspeed;

  public static void main(String[] args){
  }  

  //////////////////////////////////////
  // Initializing the applet

  public void init(){  

    // find out the language to use
    used_language = getParameter("LANGUAGE");
    // Default language is English
    language = 0;
    if (used_language.equals("German"))
      language = 1;

    // Assign the deceleration prob's
    p_dec = new double[MAXSPEED+1];
    global_p_dec = P_DEC;
    global_p_dec_inc_sts = P_DEC_INC_STS;
    global_p_dec_inc_tsqr = P_DEC_INC_TSQR;
    p_dec[0] = 0.25;
    p_dec[1] = 0.20;
    p_dec[2] = 0.17;
    p_dec[3] = 0.14;
    p_dec[4] = 0.12;
    p_dec[5] = 0.10;
    p_dec[6] = 0.08;
    p_dec[7] = 0.07;
    p_dec[8] = 0.06;
    
    // for (int v=0;v<=MAXSPEED;v++)
    // p_dec[v] = global_p_dec;
    
    
    maxspeed = MAXSPEED_INIT;

    // scrollbars of simulation speed, maxspeed and global density
    scrollbar_simspeed = new Scrollbar(Scrollbar.HORIZONTAL,SPEED_INIT,1,1,100);
    scrollbar_maxspeed = new Scrollbar(Scrollbar.HORIZONTAL,MAXSPEED_INIT,1,1,MAXSPEED);
    
    scrollbar_density = new Scrollbar(Scrollbar.HORIZONTAL,DENS_INIT,1,0,100);
        

    // Init the message boxes, the panel and the buttons
    vdr_box_0 = new MessageBox(global_p_dec,language);
    vdr_box_1 = new MessageBox(global_p_dec,global_p_dec_inc_tsqr,language);
    vdr_box_2 = new MessageBox(p_dec,maxspeed,MAXSPEED+1,language);
    vdr_box_3 = new MessageBox(global_p_dec,maxspeed,MAXSPEED+1,language);

    p = new Panel();
    switch(language){
    case 1:
      clear = new Button("Neue Diagramme");
      vdr = new Button("Infos & Details ...");
      break;
      // Default language is English
    default:
      clear = new Button("Clear Diagram");
      vdr = new Button("Info & Details ...");
      break;
    }

    // Set the model type, starting with the Standard-Nagel-Schreckenberg-
    // configuration
    modeltype = 0;
    
    // Pull-Down-menu and the filling of the panels
    model = new Choice();
    model.addItem("Standard CA");
    model.addItem("Takayasu");
    model.addItem("VDR");
    model.addItem("Fukui-Ishibashi");
    
    p.setLayout(new GridLayout(4,4));
    switch(language){
    case 1:
      label_simspeed_name = new Label("Sim.-Geschwindigkeit");
      label_maxspeed = new Label((new Integer(MAXSPEED_INIT).toString())+" Zellen/Zeitschritt");
      label_maxspeed_name = new Label("Maximalgeschwindigkeit");
      label_density_name = new Label("Globale Dichte");
      break;
      // Default language is English
    default:
      label_simspeed_name = new Label("Simulation speed");
      label_maxspeed = new Label((new Integer(MAXSPEED_INIT).toString())+" sites/timestep");
      label_maxspeed_name = new Label("Maximum velocity");
      label_density_name = new Label("Global Density");
      break;
    }

    p.add(label_simspeed_name);
    label_simspeed_name.setAlignment(Label.RIGHT);
    p.add(scrollbar_simspeed);
    label_simspeed = new Label((new Integer(SPEED_INIT).toString())+" %");
    p.add(label_simspeed);
    p.add(model);

    p.add(label_maxspeed_name);
    label_maxspeed_name.setAlignment(Label.RIGHT);
    p.add(scrollbar_maxspeed);
    p.add(label_maxspeed);
    p.add(vdr);

    p.add(label_density_name);
    label_density_name.setAlignment(Label.RIGHT);
    p.add(scrollbar_density);
    label_density = new Label((new Integer(DENS_INIT).toString())+" %");
    p.add(label_density);
    p.add(clear);

    for (int i=0;i<4;i++)
      p.add(new Label("  "));

    // Init the canvas
    canvas = new RoadCanvas(getFirstDensity(),language);

    // Design the table
    setLayout(new BorderLayout());
    add("North",p);
    add("Center",canvas);
  }
  
  ///////////////////////////////////////
  // Starting the applet
  
  public void start(){  
    if (runner == null){  
      runner = new Thread(this);
      runner.start();
    }
    else 
      if (runner.isAlive())
	runner.resume();
    clear.requestFocus();
  }

  public void stop(){
    runner.suspend();
  }

  public void destroy(){
  }

  ///////////////////////////////////////
  // Make the applet running
  
  public void run(){  
    // loop counter
    // indicates whether the diagrams have to be plotted
    int counter = 0;
    while (true){
      if (vdr_box_0.isRunning){
	if (global_p_dec != vdr_box_0.getlocal_p_dec()){
	  global_p_dec = vdr_box_0.getlocal_p_dec();
	  for (int v=0;v<=maxspeed;v++)
	    p_dec[v] = global_p_dec;
	}
      }
      else{
        if (vdr_box_1.isRunning){
          if (global_p_dec != vdr_box_1.getlocal_p_dec()){
            global_p_dec = vdr_box_1.getlocal_p_dec();
            for (int v=0;v<=maxspeed;v++)
              p_dec[v] = global_p_dec;
          }
          if (global_p_dec_inc_tsqr != vdr_box_1.getlocal_p_dec_inc())
            global_p_dec_inc_tsqr = vdr_box_1.getlocal_p_dec_inc();
        }
        else{
          if (vdr_box_2.isRunning){
            for (int v=0;v<=maxspeed;v++)
              p_dec = vdr_box_2.getlocal_p_dec_array();
          }
          else{
            if (vdr_box_3.isRunning){
              if (global_p_dec != vdr_box_3.getlocal_p_dec()){
                global_p_dec = vdr_box_3.getlocal_p_dec();
                for (int v=0;v<=maxspeed;v++)
                  p_dec[v] = 0.0;
                p_dec[maxspeed] = global_p_dec;
              }
            }
            else{
              model.enable(true);
              scrollbar_maxspeed.enable(true);
            }
          }
        }
      }
      maxspeed = getMaxspeed();
      canvas.update(p_dec,getDensity(),counter,modeltype,global_p_dec_inc_sts,global_p_dec_inc_tsqr,maxspeed);
      if (++counter >= UPDATE_DIAGRAM)
	counter = 0;
      try{
	Thread.sleep(getSimSpeed());
      }
      catch(InterruptedException e){}
    }
  }

  
  ///////////////////////////////////////
  // Handling the events

  public boolean action(Event evt, Object arg){  
    
    // Buttons
    if (evt.target instanceof Button){
      if (evt.target==clear){
	canvas.ClearDiagrams();
	return true;
      }
      if (evt.target==vdr){
	model.enable(false);
	// Model types:
	// 0: Standard model
	// 1: T²-model
	// 2: VDR-model
	// 3: Fukui-Ishibashi-model
	switch(modeltype){
	case 0:{
	  vdr_box_0.show(global_p_dec,language);
	  break;
	}
	case 1:{
	  vdr_box_1.show(global_p_dec,global_p_dec_inc_tsqr,language);
	  break;
	}
	case 2:{
	  scrollbar_maxspeed.enable(false);
	  vdr_box_2.show(p_dec,maxspeed,MAXSPEED+1,language);
	  break;
	}
	case 3:{
	  vdr_box_3.show(global_p_dec,maxspeed,MAXSPEED+1,language);
	  break;
	}
	}
	return true;
      }
    }

    // Pull-Down Menu
    if (evt.target instanceof Choice){
      if ("Standard CA".equals(arg)){
	// Setting the model parameters:
	// p_dec <> p_dec(v)
	modeltype = 0;
	global_p_dec = P_DEC;
	for (int v=0;v<=maxspeed;v++)
	  p_dec[v] = global_p_dec;
	return true;
      }
      if ("Takayasu".equals(arg)){
	// No modifications necessary, done in the update routine
	modeltype = 1;
	global_p_dec = P_DEC;
	global_p_dec_inc_tsqr = P_DEC_INC_TSQR;
	for (int v=0;v<=maxspeed;v++)
	  p_dec[v] = global_p_dec;
	return true;
      }
      if ("VDR".equals(arg)){
	// p_dec = p_dec(v) (default)
	modeltype = 2;
	for (int v=0;v<=MAXSPEED;v++){
	  p_dec[v] = P_DEC;
	}
	return true;
      }
      if ("Fukui-Ishibashi".equals(arg)){
	// p_dec = p_dec(v) (default)
	modeltype = 3;
	global_p_dec = P_DEC;
	for (int v=0;v<=maxspeed;v++)
	  p_dec[v] = 0.0;
	p_dec[maxspeed] = global_p_dec;
	return true;
      }
    }
    return false;
  }
  
  //////////////////////////////////////
  // get the actual simulation speed
  
  public int getSimSpeed(){  
    int s =  scrollbar_simspeed.getValue();
    if (s != simspeed_old){
      simspeed_old = s;
      label_simspeed.setText((new Integer(s).toString())+" %");
    }
    return (int)(5*(100-s));
  }
  
  //////////////////////////////////////
  // get the actual maxspeed
  
  public int getMaxspeed(){  
    int maxs =  scrollbar_maxspeed.getValue();
    if (maxs != maxspeed_old){
      maxspeed_old = maxs;
      switch(language){
      case 1:
        label_maxspeed.setText((new Integer(maxs).toString())+" Zellen/Zeitschritt");
        break;
        // Default language is English
      default:
        label_maxspeed.setText((new Integer(maxs).toString())+" sites/timestep");
        break;
      }
    }
    return maxs;
  }
  
  /////////////////////////////////////////////////
  // get the global density for the first time
  
  public double getFirstDensity(){  
    int dens =  scrollbar_density.getValue();
    if (dens != density_old)
      density_old = dens;
    return dens*0.01;
  }  
  
  ///////////////////////////////////////
  // get the actual global density value
  
  public double getDensity(){  
    int dens =  scrollbar_density.getValue();
    if (dens != density_old){
      density_old = dens;
      label_density.setText((new  Integer(dens).toString())+" %");
    }
    return dens*0.01;
  }
  
}  // End of "public class CAmodel extends Applet implements Runnable"
