#ifndef __net_sourceforge_jnipp_LibraryLoadException_H
#define __net_sourceforge_jnipp_LibraryLoadException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class LibraryLoadException : public BaseException
			{
			public:
				LibraryLoadException(const std::string& libName)
					: BaseException( "Failed to load library " + libName )
				{
				}
			};
		}
	}
}

#endif
