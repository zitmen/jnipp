#ifndef __net_sourceforge_jnipp_EnvironmentAlreadyInitializedException_H
#define __net_sourceforge_jnipp_EnvironmentAlreadyInitializedException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class EnvironmentAlreadyInitializedException : public BaseException
			{
			public:
				EnvironmentAlreadyInitializedException()
					: BaseException( "Environment already initialized" )
				{
				}
			};
		}
	}
}

#endif
