#ifndef __net_sourceforge_jnipp_JVMException_H
#define __net_sourceforge_jnipp_JVMException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class JVMException : public BaseException
			{
			public:
				JVMException(const std::string& message)
					: BaseException( message )
				{
				}
			};
		}
	}
}

#endif
