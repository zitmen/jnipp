#ifndef __net_sourceforge_jnipp_BaseException_H
#define __net_sourceforge_jnipp_BaseException_H

#include <string>

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class BaseException
			{
			protected:
				std::string msg;

			public:
				BaseException()
				{
				}

				BaseException(const std::string& msg)
					: msg( msg )
				{
				}

				std::string getMessage()
				{
					return msg;
				}
			};
		}
	}
}

#endif
