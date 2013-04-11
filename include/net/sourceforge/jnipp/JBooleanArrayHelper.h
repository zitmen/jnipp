#ifndef __net_sourceforge_jnipp_JBooleanArrayHelper_H
#define __net_sourceforge_jnipp_JBooleanArrayHelper_H

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
			class ComputeBooleanElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeBooleanElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeBooleanElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JBooleanArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JBooleanArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JBooleanArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeBooleanElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JBooleanArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JBooleanArrayHelper(JBooleanArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JBooleanArrayHelper<dims>& operator=(JBooleanArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JBooleanArrayHelper()
				{
				}
				
				JBooleanArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JBooleanArrayHelper<dims - 1>& value)
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
			
			class JBooleanArrayHelper<1>
			{
			private:
				jsize length;
				jbooleanArray nativeArray;
				jboolean* elems;
				bool dirty;
			
			public:
				JBooleanArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JBooleanArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewBooleanArray( length );
					elems = JNIEnvHelper::GetBooleanArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JBooleanArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jbooleanArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetBooleanArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JBooleanArrayHelper(JBooleanArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JBooleanArrayHelper<1>& operator=(JBooleanArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JBooleanArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseBooleanArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jboolean getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jboolean value)
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
				
				operator jbooleanArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseBooleanArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseBooleanArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
