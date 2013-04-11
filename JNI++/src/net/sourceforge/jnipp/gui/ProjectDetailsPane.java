package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.project.*;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.gui.appevent.*;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

/**
 * Pane for project details and properties.
 *
 * This subclass of JTabbedPane provides the display and input of project 
 * properties and settings.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.8 $
 */
public class ProjectDetailsPane extends JTabbedPane implements
	AppEventSubscriber, ActionListener, KeyListener
{
	protected JTextField txtProject_targetname;
	protected JComboBox lstProject_targettype;
	protected JTextField txtProject_outputdir;
	protected JTextField txtProject_name;
	protected JTextField txtProject_nmakesettings;
	protected JTextField txtProject_gnumakesettings;
	protected JCheckBox chkPeer_doit;
	protected JCheckBox chkPeer_isDestructive;
	protected JCheckBox chkProxy_doit;
	protected JCheckBox chkProxy_generateInnerClasses;
	protected JCheckBox chkProxy_useRichTypes;
	protected JCheckBox chkProxy_useInheritance;
	protected JCheckBox chkProxy_generateSetters;
	protected JCheckBox chkProxy_generateGetters;
	
	protected boolean _peergenNull;
	protected boolean _proxygenNull;
	
	protected ProjectClass _activeClass = null;

	/**
	 * The default constructor creates the tabs and initializes the
	 * controls and AppEvent subscriptions
	 */
	public ProjectDetailsPane()
	{
		super();
		
		this.addTab("Project", createProjectPanel());
		this.addTab("Peergen", createPeergenPanel());
		this.addTab("Proxygen", createProxygenPanel());
		this.setSelectedIndex(0);
		populateProjectSettings(App.getProject());
		initFieldListeners();
		subscribeToUpdates();
	}

	/**
	 * This method creates the panel used for project specific settings.
	 *
	 * @return A JPanel component containing necessary controls for project
	 * settings.
	 * @see net.sourceforge.jnipp.project.Project
	 */
	protected Component createProjectPanel()
	{
		//create panel
		JPanel panel = new JPanel(false);
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(myLayout);

		c.ipadx = 2;
		c.ipady = 2;
		c.insets = new Insets(5,5,5,5);
		
		//project name
		JLabel nameLabel = new JLabel("Project : ");
		txtProject_name = new JTextField();

		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(nameLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(txtProject_name,c);

		panel.add(nameLabel);
		panel.add(txtProject_name);
		
		//output directory
		JLabel outputdirLabel = new JLabel("Output Directory : ");
		txtProject_outputdir = new JTextField();

		c.gridy = 1; //drop down a row
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		myLayout.setConstraints(outputdirLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(txtProject_outputdir,c);

		panel.add(outputdirLabel);
		panel.add(txtProject_outputdir);

		//Target Name
		JLabel targetnameLabel = new JLabel("Target name : ");
		txtProject_targetname = new JTextField();

		c.gridy = 2; //drop down a row
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		myLayout.setConstraints(targetnameLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(txtProject_targetname,c);

		panel.add(targetnameLabel);
		panel.add(txtProject_targetname);
	
		//Target Type
		JLabel targettypeLabel = new JLabel("Target type : ");
		lstProject_targettype = new JComboBox();
		lstProject_targettype.addItem(ProjectAdapter.TargetType_SHLIB);
		lstProject_targettype.addItem(ProjectAdapter.TargetType_EXE);
		
		c.gridy = 3; //drop down a row
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		myLayout.setConstraints(targettypeLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(lstProject_targettype,c);

		panel.add(targettypeLabel);
		panel.add(lstProject_targettype);
		
		//NMakefileSettings
		JLabel nmakefileLabel = new JLabel("NMake Settings : ");
		txtProject_nmakesettings = new JTextField();
		
		c.gridy = 4; //drop down a row
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		myLayout.setConstraints(nmakefileLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(txtProject_nmakesettings,c);

		panel.add(nmakefileLabel);
		panel.add(txtProject_nmakesettings);

		//GNUMakefileSettings
		JLabel gnumakefileLabel = new JLabel("GNUMake Settings : ");
		txtProject_gnumakesettings = new JTextField();
		
		c.gridy = 5; //drop down a row
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		myLayout.setConstraints(gnumakefileLabel,c);
		
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(txtProject_gnumakesettings,c);

		panel.add(gnumakefileLabel);
		panel.add(txtProject_gnumakesettings);

		return panel;
	}

	/**
	 * This method creates the panel used for peergen specific settings.
	 *
	 * @return A JPanel component containing necessary controls for peergen
	 * settings.
	 * @see net.sourceforge.jnipp.project.PeerGenSettings
	 */
	protected Component createPeergenPanel()
	{
		//create panel
		JPanel panel = new JPanel(false);
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(myLayout);

		c.ipadx = 2;
		c.ipady = 2;
		c.insets = new Insets(5,5,5,5);
		
		chkPeer_doit = new JCheckBox("Perform Peer Generation");
		
		chkPeer_isDestructive = new JCheckBox("Is Destructive");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkPeer_doit,c);
		
		c.gridy += 1;
		myLayout.setConstraints(chkPeer_isDestructive,c);
		
		panel.add(chkPeer_doit);
		panel.add(chkPeer_isDestructive);

		return panel;
	}

	/**
	 * This method creates the panel used for proxygen specific settings.
	 *
	 * @return A JPanel component containing necessary controls for proxygen
	 * settings.
	 * @see net.sourceforge.jnipp.project.ProxyGenSettings
	 */
	protected Component createProxygenPanel()
	{
		//create panel
		JPanel panel = new JPanel(false);
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(myLayout);

		c.ipadx = 2;
		c.ipady = 2;
		c.insets = new Insets(5,5,5,5);
		
		chkProxy_doit = new JCheckBox("Perform Proxy Generation");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_doit,c);
		
		panel.add(chkProxy_doit);

		//
		c.gridy += 1;
		chkProxy_generateGetters = new JCheckBox("Generate attribute getters");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_generateGetters,c);
		
		panel.add(chkProxy_generateGetters);

		//
		c.gridy += 1;
		chkProxy_generateSetters = new JCheckBox("Generate attribute setters");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_generateSetters,c);
		
		panel.add(chkProxy_generateSetters);

		//
		c.gridy += 1;
		chkProxy_useInheritance = new JCheckBox("Use inheritance");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_useInheritance,c);
		
		panel.add(chkProxy_useInheritance);

		//
		c.gridy += 1;
		chkProxy_useRichTypes = new JCheckBox("Use Rich Types");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_useRichTypes,c);
		
		panel.add(chkProxy_useRichTypes);

		//
		c.gridy += 1;
		chkProxy_generateInnerClasses = new JCheckBox("Generate inner classes");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 0;
		myLayout.setConstraints(chkProxy_generateInnerClasses,c);
		
		panel.add(chkProxy_generateInnerClasses);
		
		return panel;
	}

	/**
	 * This method populates the ProjectDetailsPane with the project 
	 * settings and Peer and ProxyGenSettings
	 *
	 * @param project The project whose settings should be loaded.
	 * @see net.sourceforge.jnipp.gui.project.Project
	 */
	protected void populateProjectSettings(ProjectAdapter project)
	{
		if (project != null)
		{
			txtProject_targetname.setText(project.getTargetName());
			lstProject_targettype.setSelectedItem(project.getTargetType());
			txtProject_outputdir.setText(project.getOutputDir());
			txtProject_name.setText(project.getName());
			txtProject_nmakesettings.setText(project.getNMakeFileSettings());
			txtProject_gnumakesettings.setText(project.getGNUMakeFileSettings());
		}
	}

	/**
	 * This method populates the settings tabs with the ProjectClass object 
	 *
	 * @param proxygenset The ProxyGenSettings object that will be used.
	 * @see net.sourceforge.jnipp.gui.project.ProxyGenSettings
	 */
	protected void populateClassSettings(ProjectClass activeClass)
	{
		if (activeClass == null)
		{
			return;
		}
		
		ProjectSettingsAdapter settings = activeClass.getProjectSettingsAdapter();
		if (settings.getDoProxyGen() == true)
		{
			chkProxy_doit.setSelected(true);
			chkProxy_generateInnerClasses.setSelected(settings.getProxyGenerateInnerClasses());
			chkProxy_useRichTypes.setSelected(settings.getProxyUseRichTypes());
			chkProxy_useInheritance.setSelected(settings.getProxyUseInheritance());
			chkProxy_generateSetters.setSelected(settings.getProxyGenerateSetters());
			chkProxy_generateGetters.setSelected(settings.getProxyGenerateGetters());
		}
		else
		{
			//TODO: put these in a frame that can be disabled and
			//enabled...
			chkProxy_doit.setSelected(false);
			chkProxy_generateInnerClasses.setSelected(false);
			chkProxy_useRichTypes.setSelected(false);
			chkProxy_useInheritance.setSelected(false);
			chkProxy_generateSetters.setSelected(false);
			chkProxy_generateGetters.setSelected(false);
		}
		
		if (settings.getDoPeerGen() == true)
		{
			chkPeer_doit.setSelected(true);
			chkPeer_isDestructive.setSelected(settings.getPeerIsDestructive());
		}
		else
		{
			//TODO: put these in a frame that can be disabled and
			//enabled...
			chkPeer_doit.setSelected(false);
			chkPeer_isDestructive.setSelected(false);
		}
	}

	/**
	 * This method implements the AppEventSubscriber interface. The 
	 * ProjectDetailsPane responds to the UserContextUpdatedAppEvent
	 * and the ProjectUpdatedAppEvent.
	 *
	 * @param e The AppEvent object that was fired.
	 * @see net.sourceforge.jnipp.gui.appevent.UserContextUpdatedAppEvent
	 * @see net.sourceforge.jnipp.gui.appevent.ProjectUpdatedAppEvent
	 * @see net.sourceforge.jnipp.gui.appevent.AppEventSubscriber
	 */
	public void UpdateAvailable(AppEvent e)
	{
		//TODO: replace instanceof likeness with something polymorphic ?
		
		if (e.getClass() == UserContextUpdatedAppEvent.class)
		{
			UserContextUpdatedAppEvent subject = (UserContextUpdatedAppEvent) e;
			UserContext context = subject.getContext();
			UserContext.ContextType contextType = context.getActiveContext();
			if (contextType == UserContext.ContextType_Project)
			{
				this.setSelectedIndex(0);
				this.setEnabledAt(1, false);
				this.setEnabledAt(2, false);
				_activeClass = null;
			}
			else if (contextType == UserContext.ContextType_Class)
			{
				_activeClass = (ProjectClass)context.getContextObject();
				populateClassSettings(_activeClass);
				setEnabledAt(1, true);
				setEnabledAt(2, true);
			}
		}
		else if (e.getClass() == ProjectUpdatedAppEvent.class)
		{
			ProjectUpdatedAppEvent subject = (ProjectUpdatedAppEvent) e;
			
			if (subject.getType() == ProjectUpdatedAppEvent.UpdateType_LoadProject)
			{
				populateProjectSettings(App.getProject());
			}
			else if (subject.getType() == ProjectUpdatedAppEvent.UpdateType_ChangeClasses)
			{
				//We ignore this.. just looking for context
				//changes with classes...
			}
		}
	}

	private void initFieldListeners()
	{
		txtProject_targetname.addKeyListener(this);
		lstProject_targettype.addActionListener(this);
		txtProject_outputdir.addKeyListener(this);
		txtProject_name.addKeyListener(this);
		txtProject_nmakesettings.addKeyListener(this);
		txtProject_gnumakesettings.addKeyListener(this);
		chkPeer_doit.addActionListener(this);
		chkPeer_isDestructive.addActionListener(this);
		chkProxy_doit.addActionListener(this);
		chkProxy_generateInnerClasses.addActionListener(this);
		chkProxy_useRichTypes.addActionListener(this);
		chkProxy_useInheritance.addActionListener(this);
		chkProxy_generateSetters.addActionListener(this);
		chkProxy_generateGetters.addActionListener(this);
	}

	/**
	 * This method implements ActionListener non-text fields.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		ProjectAdapter project = App.getProject();
		if (source == lstProject_targettype)
		{
			project.setTargetType((ProjectAdapter.TargetType)lstProject_targettype.getSelectedItem());
			return;
		}
		
		if (_activeClass == null)
		{
			return;
		}
		
		ProjectSettingsAdapter settings = _activeClass.getProjectSettingsAdapter();
		
		if (source == chkPeer_doit)
		{
			settings.setDoPeerGen(chkPeer_doit.isSelected());
			return;
		}
		if (source == chkProxy_doit)
		{
			settings.setDoProxyGen(chkProxy_doit.isSelected());
			return;
		}

		if (source == chkPeer_isDestructive)
		{
			if (settings.getDoPeerGen() == false)
			{
				settings.setDoPeerGen(true);
				chkPeer_doit.setSelected(true);
			}
			settings.setPeerIsDestructive(chkPeer_isDestructive.isSelected());
			return;
		}
		if (source == chkProxy_generateInnerClasses)
		{
			if (settings.getDoProxyGen() == false)
			{
				settings.setDoProxyGen(true);
				chkProxy_doit.setSelected(true);
			}
			settings.setProxyGenerateInnerClasses(chkProxy_generateInnerClasses.isSelected());
			return;
		}
		if (source == chkProxy_useRichTypes)
		{
			if (settings.getDoProxyGen() == false)
			{
				settings.setDoProxyGen(true);
				chkProxy_doit.setSelected(true);
			}
			settings.setProxyUseRichTypes(chkProxy_useRichTypes.isSelected());
			return;
		}
		if (source == chkProxy_useInheritance)
		{
			if (settings.getDoProxyGen() == false)
			{
				settings.setDoProxyGen(true);
				chkProxy_doit.setSelected(true);
			}
			settings.setProxyUseInheritance(chkProxy_useInheritance.isSelected());
			return;
		}
		if (source == chkProxy_generateSetters)
		{
			if (settings.getDoProxyGen() == false)
			{
				settings.setDoProxyGen(true);
				chkProxy_doit.setSelected(true);
			}
			settings.setProxyGenerateSetters(chkProxy_generateSetters.isSelected());
			return;
		}
		if (source == chkProxy_generateGetters)
		{
			if (settings.getDoProxyGen() == false)
			{
				settings.setDoProxyGen(true);
				chkProxy_doit.setSelected(true);
			}
			settings.setProxyGenerateGetters(chkProxy_generateGetters.isSelected());
			return;
		}
	}

	public void keyTyped(KeyEvent e)
	{
		Object source = e.getSource();
		ProjectAdapter project = App.getProject();
		if (source == txtProject_targetname)
		{
			project.setTargetName(txtProject_targetname.getText() + e.getKeyChar());
		}
		else if (source == txtProject_outputdir)
		{
			project.setOutputDir(txtProject_outputdir.getText() + e.getKeyChar());
		}
		else if (source == txtProject_name)
		{
			project.setName(txtProject_name.getText() + e.getKeyChar());
		}
		else if (source == txtProject_nmakesettings)
		{
			project.setNMakeFileSettings(txtProject_nmakesettings.getText() + e.getKeyChar());
		}
		else if (source == txtProject_gnumakesettings)
		{
			project.setGNUMakeFileSettings(txtProject_gnumakesettings.getText() + e.getKeyChar());
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}

	public void keyPressed(KeyEvent e)
	{
	}

	/**
	 * This method subscribes the ProjectDetailsPane to AppEvents it's 
	 * interested in
	 *
	 * @see net.sourceforge.jnipp.gui.appevent.UserContextUpdatedAppEvent
	 * @see net.sourceforge.jnipp.gui.appevent.ProjectUpdatedAppEvent
	 */
	private void subscribeToUpdates()
	{
		UserContextUpdatedAppEvent.subscribe(this);
		ProjectUpdatedAppEvent.subscribe(this);
	}

}
