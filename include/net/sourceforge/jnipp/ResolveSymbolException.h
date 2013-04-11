#ifndef __net_sourceforge_jnipp_ResolveSymbolException_H
#define __net_sourceforge_jnipp_ResolveSymbolException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class ResolveSymbolException : public BaseException
			{
			public:
				ResolveSymbolException(const std::string& symbolName)
					: BaseException( "Failed to resolve symbol " + symbolName )
				{
				}
			};
		}
	}
}

#endif
