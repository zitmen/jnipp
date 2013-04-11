#ifndef __net_sourceforge_jnipp_JLongArrayHelper_H
#define __net_sourceforge_jnipp_JLongArrayHelper_H

#ifdef WIN32
#pragma warning(disable:4251)
#pragma warning(disable:4786)
#endif

#include <jni.h>
#include <string>
#include "JNIEnvHelper.h"
#include "ArrayIndexOutOfBoundsException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			template<unsigned int dims>
			class ComputeLongElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeLongElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeLongElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JLongArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JLongArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JLongArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeLongElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JLongArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JLongArrayHelper(JLongArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JLongArrayHelper<dims>& operator=(JLongArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JLongArrayHelper()
				{
				}
				
				JLongArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JLongArrayHelper<dims - 1>& value)
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					JNIEnvHelper::SetObjectArrayElement( nativeArray, index, static_cast<jobject>(value) );
				}

				jsize getLength() const
				{
					return length;
				}
				
				operator jobjectArray() const
				{
					return nativeArray;
				}
				
				operator jobject() const
				{
					return nativeArray;
				}
			};
			
			class JLongArrayHelper<1>
			{
			private:
				jsize length;
				jlongArray nativeArray;
				jlong* elems;
				bool dirty;
			
			public:
				JLongArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JLongArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewLongArray( length );
					elems = JNIEnvHelper::GetLongArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JLongArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jlongArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetLongArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JLongArrayHelper(JLongArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JLongArrayHelper<1>& operator=(JLongArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JLongArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseLongArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jlong getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jlong value)
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					elems[index] = value;
					dirty = true;
				}

				jsize getLength() const
				{
					return length;
				}
				
				operator jlongArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseLongArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseLongArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
