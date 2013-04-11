package net.sourceforge.jnipp.main;
import net.sourceforge.jnipp.common.FormattedFileWriter;
import net.sourceforge.jnipp.project.GNUMakefileSettings;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * GNU makefile generator.
 *
 * This class is utilized to generate a GNU makefile for a target
 * <code>Project</code>.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.12 $
 */

public class GNUMakefileGenerator 
{
   /**
    * Generate makefile for target <code>Project</code>.
    *
    * This method is the public entry point called by the <code>Main</code> class
    * to invoke the code generator.  It will utilize the settings specified in
    * the <code>GNUMakefileSettings</code> parameter to control the output.
    *
    * @param gnuMakefileSettings The settings controlling the code generation.
    * @param peerGenClassNodes Collection of <code>ClassNode</code> instances
    * for which code was generated by the C++ Peer Generator.
    * @param proxyGenClassNodes Collection of <code>ClassNode</code> instances
    * for which code was generated by the C++ Proxy Generator.
    * @exception java.io.IOException
    * @see net.sourceforge.jnipp.main.Main
    * @see net.sourceforge.jnipp.common.ClassNode
    */
   public static void generate(GNUMakefileSettings gnuMakefileSettings, Collection peerGenDependencies, Collection proxyGenDependencies)
      throws java.io.IOException
   {
		System.out.println( "generating GNU Makefile for project " + gnuMakefileSettings.getProject().getName() + " ..." );
      FormattedFileWriter writer = new FormattedFileWriter( gnuMakefileSettings.getProject().getCPPOutputDir() + File.separator + gnuMakefileSettings.getName()  );

      writer.outputLine( "INCLUDES= -I. -I$(JNIPP_HOME)/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(OS)" );
		writer.output( "TARGETTYPE= " );
      writer.outputLine( gnuMakefileSettings.getProject().getTargetType().equals( "exe" ) == true ? "consoleapp" : "shlib" );
		writer.output( "TARGETNAME= " );
		if ( gnuMakefileSettings.getProject().getTargetType().equals( "exe" ) == false )
			writer.output( "lib" );
		writer.outputLine( gnuMakefileSettings.getProject().getTargetName() );
		writer.newLine( 1 );
		writer.outputLine( "ifeq ($(TARGETTYPE), shlib)" );
      writer.outputLine( "DBGCPPFLAGS= -O0 -g -fPIC -c -Wall $(INCLUDES)" );
      writer.outputLine( "RELCPPFLAGS= -O0 -fPIC -c -Wall $(INCLUDES)" );
		writer.outputLine( "else" );
		writer.outputLine( "DBGCPPFLAGS= -O0 -g -c -Wall $(INCLUDES)" );
      writer.outputLine( "RELCPPFLAGS= -O0 -c -Wall $(INCLUDES)" );
		writer.outputLine( "endif" );
		writer.newLine( 1 );
      writer.outputLine( "LIBPATH= -L$(JNIPP_HOME)/lib" );
      writer.outputLine( "DBGLIBS= $(LIBPATH) -lJNIPPCore_d" );
      writer.outputLine( "RELLIBS= $(LIBPATH) -lJNIPPCore" );
		writer.newLine( 1 );
		writer.outputLine( "LINKFLAGS=" );
		writer.outputLine( "EXT=" );
		writer.outputLine( "ifeq ($(TARGETTYPE), shlib)" );
      writer.outputLine( "ifeq ($(OS), linux)" );
      writer.outputLine( "LINKFLAGS=-shared" );
		writer.outputLine( "EXT=.so" );
      writer.outputLine( "else" );
      writer.outputLine( "SHLIBCMD=-G" );
		writer.outputLine( "EXT=.so" );
		writer.outputLine( "endif" );
      writer.outputLine( "endif" );
      writer.newLine( 1 );
      writer.outputLine( "%_d.o:\t%.cpp" );
      writer.outputLine( "\t\t\tg++ $(DBGCPPFLAGS) $< -o $@" );
      writer.newLine( 1 );
      writer.outputLine( "%.o:\t%.cpp" );
      writer.outputLine( "\t\t\tg++ $(RELCPPFLAGS) $< -o $@" );
      writer.newLine( 1 );
      writer.output( "SRCS= " );
      Iterator depIter = peerGenDependencies.iterator();
      while ( depIter.hasNext() == true )
      {
         DependencyData current = (DependencyData) depIter.next();
         writer.output( " " + gnuMakefileSettings.getProject().getCPPOutputDir() + File.separator + current.getFullCPPFileName() );
      }
      depIter = proxyGenDependencies.iterator();
      while ( depIter.hasNext() == true )
      {
         DependencyData current = (DependencyData) depIter.next();
         writer.output( " " + gnuMakefileSettings.getProject().getCPPOutputDir() + File.separator + current.getFullCPPFileName() );
      }
      writer.newLine( 1 );
      writer.outputLine( "DBGOBJS=$(patsubst %.cpp, %_d.o, $(SRCS))" );
      writer.outputLine( "RELOBJS=$(patsubst %.cpp, %.o, $(SRCS))" );
      writer.newLine( 1 );
      writer.outputLine( "all:\t\tDebug Release" );
      writer.newLine( 1 );
      writer.outputLine( "Debug:\tdirs $(DBGOBJS)" );
      writer.outputLine( "\t\t\tg++ $(LINKFLAGS) -o Debug/$(TARGETNAME)_d$(EXT) $(DBGOBJS) $(DBGLIBS)" );
      writer.newLine( 1 );
      writer.outputLine( "Release:\tdirs $(RELOBJS)" );
      writer.outputLine( "\t\t\tg++ $(LINKFLAGS) -o Release/$(TARGETNAME)$(EXT) $(RELOBJS) $(RELLIBS)" );
      writer.newLine( 1 );
      writer.outputLine( "dirs:\t$(dummy)" );
      writer.outputLine( "\t\t@mkdir -p Debug" );
      writer.outputLine( "\t\t@mkdir -p Release" );
      writer.newLine( 1 );
      writer.outputLine( "clean:\t$(dummy)" );
      writer.outputLine( "\t\t\t@rm -rf Debug" );
      writer.outputLine( "\t\t\t@rm -rf Release" );
      writer.newLine( 1 );
      writer.outputLine( "rebuild:	clean Debug Release" );

      writer.flush();
      writer.close();
   }
}