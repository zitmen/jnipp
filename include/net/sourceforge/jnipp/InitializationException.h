#ifndef __net_sourceforge_jnipp_InitializationException_H
#define __net_sourceforge_jnipp_InitializationException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class InitializationException : public BaseException
			{
			public:
				InitializationException(const std::string& msg)
					: BaseException( "Failed to initialize JVM: " + msg )
				{
				}
			};
		}
	}
}

#endif
