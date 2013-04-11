#ifndef __net_sourceforge_jnipp_ArrayIndexOutOfBoundsException_H
#define __net_sourceforge_jnipp_ArrayIndexOutOfBoundsException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class ArrayIndexOutOfBoundsException : public BaseException
			{
			public:
				ArrayIndexOutOfBoundsException(jsize index)
				{
					msg = "Array index out of bounds: " + index;
				}
			};
		}
	}
}

#endif
