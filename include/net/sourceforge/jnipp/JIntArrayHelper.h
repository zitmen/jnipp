#ifndef __net_sourceforge_jnipp_JIntArrayHelper_H
#define __net_sourceforge_jnipp_JIntArrayHelper_H

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
			class ComputeIntElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeIntElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeIntElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JIntArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JIntArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JIntArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeIntElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JIntArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JIntArrayHelper(JIntArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JIntArrayHelper<dims>& operator=(JIntArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JIntArrayHelper()
				{
				}
				
				JIntArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JIntArrayHelper<dims - 1>& value)
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
			
			class JIntArrayHelper<1>
			{
			private:
				jsize length;
				jintArray nativeArray;
				jint* elems;
				bool dirty;
			
			public:
				JIntArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JIntArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewIntArray( length );
					elems = JNIEnvHelper::GetIntArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JIntArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jintArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetIntArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JIntArrayHelper(JIntArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JIntArrayHelper<1>& operator=(JIntArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JIntArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseIntArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jint getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jint value)
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
				
				operator jintArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseIntArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseIntArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
