#ifndef __net_sourceforge_jnipp_JFloatArrayHelper_H
#define __net_sourceforge_jnipp_JFloatArrayHelper_H

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
			class ComputeFloatElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeFloatElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeFloatElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JFloatArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JFloatArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JFloatArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeFloatElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JFloatArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JFloatArrayHelper(JFloatArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JFloatArrayHelper<dims>& operator=(JFloatArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JFloatArrayHelper()
				{
				}
				
				JFloatArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JFloatArrayHelper<dims - 1>& value)
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
			
			class JFloatArrayHelper<1>
			{
			private:
				jsize length;
				jfloatArray nativeArray;
				jfloat* elems;
				bool dirty;
			
			public:
				JFloatArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JFloatArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewFloatArray( length );
					elems = JNIEnvHelper::GetFloatArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JFloatArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jfloatArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetFloatArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JFloatArrayHelper(JFloatArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JFloatArrayHelper<1>& operator=(JFloatArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JFloatArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseFloatArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jfloat getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jfloat value)
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
				
				operator jfloatArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseFloatArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseFloatArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
