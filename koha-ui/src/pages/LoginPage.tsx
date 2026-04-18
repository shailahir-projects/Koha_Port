import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import {
  Button,
  FormField,
  FormFieldLabel,
  Input,
  StackLayout,
  Text,
} from '@salt-ds/core';
import { login } from '../api/client';
import { useAuth } from '../components/AuthContext';
import type { LoginRequest } from '../types';

export default function LoginPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginRequest>();

  const onSubmit = async (data: LoginRequest) => {
    try {
      const resp = await login(data);
      signIn(resp.accessToken);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError('root', { message: String(err) });
    }
  };

  return (
    <StackLayout
      style={{
        minHeight: '100vh',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'var(--salt-palette-neutral-background)',
      }}
    >
      <StackLayout
        style={{
          width: 380,
          padding: 'var(--salt-spacing-300)',
          border: '1px solid var(--salt-palette-neutral-border)',
          borderRadius: 'var(--salt-curve-150)',
          background: 'var(--salt-palette-neutral-background-medium)',
        }}
        gap={2}
      >
        <Text styleAs="h2" style={{ textAlign: 'center' }}>
          Koha ILS
        </Text>
        <Text styleAs="label" style={{ textAlign: 'center', color: 'var(--salt-palette-neutral-foreground-secondary)' }}>
          Staff Portal
        </Text>

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <StackLayout gap={2}>
            <FormField validationStatus={errors.username ? 'error' : undefined}>
              <FormFieldLabel>Username</FormFieldLabel>
              <Input
                {...register('username', { required: 'Username is required' })}
                placeholder="Enter username"
              />
            </FormField>

            <FormField validationStatus={errors.password ? 'error' : undefined}>
              <FormFieldLabel>Password</FormFieldLabel>
              <Input
                {...register('password', { required: 'Password is required' })}
                inputProps={{ type: 'password' }}
                placeholder="Enter password"
              />
            </FormField>

            {errors.root && (
              <Text style={{ color: 'var(--salt-status-negative-foreground)' }}>
                {errors.root.message}
              </Text>
            )}

            <Button
              type="submit"
              appearance="solid"
              sentiment="accented"
              disabled={isSubmitting}
              style={{ width: '100%' }}
            >
              {isSubmitting ? 'Signing in…' : 'Sign In'}
            </Button>
          </StackLayout>
        </form>
      </StackLayout>
    </StackLayout>
  );
}
