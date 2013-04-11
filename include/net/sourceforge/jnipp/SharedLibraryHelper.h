#ifndef __net_sourceforge_jnipp_SharedLibraryHelper_H
#define __net_sourceforge_jnipp_SharedLibraryHelper_H

#include <string>

#ifdef WIN32
#include <windows.h>
typedef HMODULE SHLIBHANDLE;
#elif MACOSX
#include <mach-o/dyld.h>
typedef NSModule SHLIBHANDLE;
#else
#include <dlfcn.h>
typedef void* SHLIBHANDLE;
#endif

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			#ifdef WIN32
			#ifdef __IncludedFromCore
			class __declspec(dllexport) SharedLibraryHelper
			#else
			class __declspec(dllimport) SharedLibraryHelper
			#endif
			#else
			class SharedLibraryHelper
			#endif
			{
			private:
				SHLIBHANDLE handle;
				std::string libName;

			public:
				SharedLibraryHelper();
				~SharedLibraryHelper();
				void load(const std::string& libName);
				void* resolveSymbol(const std::string& symbolName);
				void unload();
			};
		}
	}
}

#endif
