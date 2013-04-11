#ifndef __net_sourceforge_jnipp_EnvironmentNotInitializedException_H
#define __net_sourceforge_jnipp_EnvironmentNotInitializedException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class EnvironmentNotInitializedException : public BaseException
			{
			public:
				EnvironmentNotInitializedException()
					: BaseException( "Environment not initialized" )
				{
				}
			};
		}
	}
}

#endif
