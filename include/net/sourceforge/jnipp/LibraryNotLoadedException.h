#ifndef __net_sourceforge_jnipp_LibraryNotLoadedException_H
#define __net_sourceforge_jnipp_LibraryNotLoadedException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class LibraryNotLoadedException : public BaseException
			{
			public:
				LibraryNotLoadedException()
					: BaseException( "Library not loaded" )
				{
				}
			};
		}
	}
}

#endif
